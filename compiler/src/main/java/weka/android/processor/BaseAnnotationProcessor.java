package weka.android.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Base {@link javax.annotation.processing.Processor}.
 *
 * @author Jin, Heonkyu <heonkyu.jin@gmail.com>
 */

@SupportedSourceVersion(SourceVersion.RELEASE_7)
abstract class BaseAnnotationProcessor extends AbstractProcessor {

    /**
     *
     */
    private PackageWorkingSet mPackageWorkingSet = new PackageWorkingSet();
    private ClassWorkingSet mClassWorkingSet = new ClassWorkingSet();

    Types typeUtils;
    Elements elementUtils;
    Filer filer;
    Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mPackageWorkingSet = new PackageWorkingSet();
        mClassWorkingSet = new ClassWorkingSet();

        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    boolean checkFieldAnnotation(Class annotationClass, Element element) {
        if (element.getKind().isField()) {
            if (element.getModifiers().contains(Modifier.PRIVATE)) {
                error(element, "Annotated field %s should not be private",
                        element.getSimpleName());
                return false;
            }
        } else {
            error(element, "Only field variables can be annotated with @%s",
                    annotationClass.getSimpleName());
            return false;
        }

        return true;
    }

    boolean checkTypeAnnotation(Class annotationClass, Element element) {
        if (element.getKind().isClass()) {
            if (element.getModifiers().contains(Modifier.PRIVATE)) {
                error(element, "Annotated class %s should not be private",
                        element.getSimpleName());
                return false;
            }
        } else {
            error(element, "Only class variables can be annotated with @%s",
                    annotationClass.getSimpleName());
            return false;
        }

        return true;
    }

    void addToSets(VariableElement element) {
        PackageElement packageElement = elementUtils.getPackageOf(element);
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();

        mPackageWorkingSet.add(packageElement, typeElement);
        mClassWorkingSet.add(typeElement, element);
    }

    void addToSets(TypeElement element) {
        PackageElement packageElement = elementUtils.getPackageOf(element);

        mPackageWorkingSet.add(packageElement, element);
    }

    void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    PackageWorkingSet getPackageWorkingSet() {
        return mPackageWorkingSet;
    }

    ClassWorkingSet getClassWorkingSet() {
        return mClassWorkingSet;
    }

    /**
     * Working set of packages whose enclosed elements are annotated.
     */
    static class PackageWorkingSet extends ElementWorkingSet<PackageElement, TypeElement> {
    }

    /**
     * Working set of classes whose enclosed elements are annotated.
     */
    static class ClassWorkingSet extends ElementWorkingSet<TypeElement, VariableElement> {
    }
}