package recsys.domain;

import org.junit.Test;
import recsys.core.Data;
import recsys.core.SimilarityFunction;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Anton Jansson.
 */
public class UserSimilarityTest {

    // Mock data container
    private final Data<Integer, Integer> data = new Data<Integer, Integer>() {
        @Override
        public Map<Integer, Integer> getRatings(Integer user) {
            Map<Integer, Integer> map = new HashMap<>();
            if (user == 1) {
                map.put(1, 1);
                map.put(2, 2);
                map.put(3, 3);
                map.put(4, 4);
            }
            if (user == 2) {
                map.put(2, 2);
                map.put(3, 3);
                map.put(4, 4);
            }
            if (user == 3) {
                map.put(1, 4);
                map.put(2, 3);
                map.put(3, 2);
                map.put(4, 1);
            }
            return map;
        }

        @Override
        public Integer getRating(Integer integer, Integer integer2) {
            return null;
        }
    };

    /** Precision for floating number tests. Expected result must be this accurate. */
    private static final double PREC = 0.00000000001;

    private final SimilarityFunction<Integer> similarity = new UserSimilarity();

    @Test
    public void similarity() throws Exception {
        // Expected similarities calculated at http://www.appliedsoftwaredesign.com/archives/cosine-similarity-calculator
        assertEquals("Same user", 1, similarity.similarity(1, 1, data), PREC);
        assertEquals("No similarity", 0, similarity.similarity(1, 0, data), PREC);
        assertEquals(0.983192080250, similarity.similarity(1, 2, data), PREC);
        assertEquals(0.666666666667, similarity.similarity(1, 3, data), PREC);
        assertEquals(0.542450802897, similarity.similarity(3, 2, data), PREC);
    }

}