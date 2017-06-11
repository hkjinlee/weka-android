package weka.android.processor.test;

import org.junit.Before;
import org.junit.Test;

import weka.android.Weka;
import weka.android.annotation.Safe;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jin, Heonkyu <heonkyu.jin@gmail.com> on 2017. 6. 11.
 */

public class SafeAnnotationTest {
    private SafeHolder safeHolder;

    @Before
    public void setUp() {
        safeHolder = new SafeHolder();
    }

    @Test
    public void testSafeAnnotation() {
        assertThat(safeHolder.safeFilter.getDoNotCheckCapabilities()).isTrue();
        assertThat(safeHolder.unsafeFilter.getDoNotCheckCapabilities()).isFalse();
    }

    class SafeHolder {
        @Safe
        Filter safeFilter = new Normalize();

        Filter unsafeFilter = new Normalize();

        public SafeHolder() {
            Weka.safeSetup(this);
        }
    }
}
