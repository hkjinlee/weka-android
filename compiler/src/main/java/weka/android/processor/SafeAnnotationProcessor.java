package weka.android.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import weka.android.SafeAnnotated;
import weka.android.AnnotationUtil;
import weka.android.annotation.Safe;

/**
 * Processor for {@link Safe} annotation.
 * Generates one .java file for each of classes whose member variables are annotated by {@link Safe}.
 *
 * Generated source files belong to the same package of the annotated classes,
 * to minimize the effort of making member variables public.
 *
 * @author Jin, Heonkyu <heonkyu.jin@gmail.com>
 */

@SupportedAnnotationTypes({
        "weka.android.annotation.Safe"
})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@AutoService(Processor.class)
public final class SafeAnnotationProcessor extends BaseAnnotationProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Safe.class)) {
            if (!checkFieldAnnotation(Safe.class, element)) {
                return true;
            }
            addToSets((VariableElement) element);
        }

        try {
            PackageWorkingSet packageSet = getPackageWorkingSet();
            for (PackageElement packageElement : packageSet.getItems()) {
                for (TypeElement classElement : packageSet.getSubItems(packageElement)) {
                    generateCodePerClass(packageElement, classElement);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void generateCodePerClass(PackageElement packageElement, TypeElement classElement) throws IOException {
        Name packageName = packageElement.getQualifiedName();
        String codeClassName = AnnotationUtil.getSetupClassName(classElement.getSimpleName().toString());

        TypeSpec.Builder setupClass = TypeSpec.classBuilder(codeClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ParameterizedTypeName.get(
                        ClassName.get(SafeAnnotated.class),
                        ClassName.get(classElement)
                ));

        MethodSpec.Builder setupMethodBuilder = MethodSpec
                .methodBuilder(SafeAnnotated.SETUP_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(classElement), "instance")
                .returns(TypeName.VOID);

        ClassWorkingSet classSet = getClassWorkingSet();
        for (VariableElement variable : classSet.getSubItems(classElement)) {
            Name varName = variable.getSimpleName();
            String varClass = variable.asType().toString();
            setupMethodBuilder.addStatement("instance.$N.setDoNotCheckCapabilities(true)", varName);
        }

        setupClass.addMethod(setupMethodBuilder.build());

        JavaFile.builder(packageName.toString(), setupClass.build()).build().writeTo(filer);
    }
}
