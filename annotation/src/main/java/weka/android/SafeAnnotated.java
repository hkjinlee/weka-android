package weka.android;

/**
 * @author Jin, Heonkyu <heonkyu.jin@gmail.com>
 */

public interface SafeAnnotated<T> {
    String SETUP_METHOD_NAME = "setup";

    void setup(T obj);
}
