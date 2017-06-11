package weka.android.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
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

import weka.android.AnnotationUtil;
import weka.android.annotation.Instance;
import weka.android.annotation.NominalAttribute;
import weka.android.annotation.NumericAttribute;

/**
 * @author Jin, Heonkyu <heonkyu.jin@gmail.com> on 2017. 5. 27.
 */

@SupportedAnnotationTypes({
        "weka.android.annotation.Instance",
        "weka.android.annotation.NominalAttribute",
        "weka.android.annotation.NumericAttribute"
})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@AutoService(Processor.class)
public final class InstanceAnnotationProcessor extends BaseAnnotationProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Instance.class)) {
            if (!checkTypeAnnotation(Instance.class, element)) {
                return true;
            }
            addToSets((TypeElement) element);
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(NumericAttribute.class)) {
            if (!checkFieldAnnotation(NumericAttribute.class, element)) {
                return true;
            }
            addToSets((VariableElement) element);
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(NominalAttribute.class)) {
            if (!checkFieldAnnotation(NominalAttribute.class, element)) {
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
        String codeClassName = AnnotationUtil.getInstanceClassName(classElement.getSimpleName().toString());

        TypeSpec.Builder enclosingClass = TypeSpec.classBuilder(codeClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        MethodSpec.Builder initMethodBuilder = MethodSpec
                .methodBuilder("asdf")//AnnotationUtil.getSetupMethodName(classElement.getSimpleName().toString()))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get(classElement), "instance")
                .returns(TypeName.VOID);

        ClassWorkingSet classSet = getClassWorkingSet();
        for (VariableElement variable : classSet.getSubItems(classElement)) {
            Name varName = variable.getSimpleName();
            String varClass = variable.asType().toString();
            initMethodBuilder.addComment("$N", variable.getAnnotation(NominalAttribute.class));
            initMethodBuilder.addStatement("instance.$N.setDoNotCheckCapabilities(true)", varName);
        }

        enclosingClass.addMethod(initMethodBuilder.build());

        JavaFile.builder(packageName.toString(), enclosingClass.build()).build().writeTo(filer);
    }
}
