package weka.android;

import java.util.HashMap;
import java.util.Map;

import weka.core.Instances;

/**
 * @author Jin, Heonkyu <heonkyu.jin@gmail.com> on 2017. 6. 11.
 */

public class Weka {
    private static Map<String, SafeAnnotated> setupMap = new HashMap<>();

    public static void safeSetup(Object annotatedObj) {
        try {
            SafeAnnotated safeAnnotated = getSafeSetupInstance(annotatedObj);
            safeAnnotated.setup(annotatedObj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static SafeAnnotated getSafeSetupInstance(Object annotatedObj) throws
            ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class annotatedClass = annotatedObj.getClass();
        String canonicalName = annotatedClass.getCanonicalName();

        if (!setupMap.containsKey(canonicalName)) {
            String packageName = annotatedClass.getPackage().getName();
            String setupClassName = AnnotationUtil.getSetupClassName(annotatedClass.getSimpleName());
            Class setupClass = Class.forName(packageName + "." + setupClassName);

            SafeAnnotated instance = (SafeAnnotated) setupClass.newInstance();
            setupMap.put(canonicalName, instance);
        }

        return setupMap.get(canonicalName);
    }

    public static Instances buildInstances(Class instance) {
        return null;
    }
}
