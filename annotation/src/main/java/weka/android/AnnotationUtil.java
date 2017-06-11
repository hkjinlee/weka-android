package weka.android;

/**
 * @author Jin, Heonkyu <heonkyu.jin@gmail.com> on 2017. 6. 11.
 */

public class AnnotationUtil {
    private static final String SETUP_CLASS_PREFIX = "Setup";

    private static final String INSTANCE_CLASS_PREFIX = "Instance";

    public static String getSetupClassName(String className) {
        return SETUP_CLASS_PREFIX + className;
    }

    public static String getInstanceClassName(String className) {
        return INSTANCE_CLASS_PREFIX + className;
    }
}
