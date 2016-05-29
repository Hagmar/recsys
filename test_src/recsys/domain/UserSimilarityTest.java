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

    /** Precision for floating number tests. Expected result must be this accurate. */
    private static final double PREC = 0.00000000001;

    private final User u0 = new User(0),
            u1 = new User(1),
            u2 = new User(2),
            u3 = new User(3);

    // Mock data container
    private final Data<User, Movie> data = new InMemoryData(createData());

    private Map<User, Map<Movie, Double>> createData() {
        Map<User, Map<Movie, Double>> result = new HashMap<>();
        Map<Movie, Double> map;

        map = new HashMap<>();
        map.put(new Movie(1), 1d);
        map.put(new Movie(2), 2d);
        map.put(new Movie(3), 3d);
        map.put(new Movie(4), 4d);
        result.put(u1, map);


        map = new HashMap<>();
        map.put(new Movie(2), 2d);
        map.put(new Movie(3), 3d);
        map.put(new Movie(4), 4d);
        result.put(u2, map);


        map = new HashMap<>();
        map.put(new Movie(1), 4d);
        map.put(new Movie(2), 3d);
        map.put(new Movie(3), 2d);
        map.put(new Movie(4), 1d);
        result.put(u3, map);

        return result;
    }

    private final SimilarityFunction<User> similarity = new UserSimilarity();

    @Test
    public void similarity() throws Exception {
        // Expected similarities calculated at http://www.appliedsoftwaredesign.com/archives/cosine-similarity-calculator
        assertEquals("Same user", 1, similarity.similarity(u1, u1, data), PREC);
        assertEquals("No similarity", 0, similarity.similarity(u1, u0, data), PREC);
        assertEquals(0.983192080250, similarity.similarity(u1, u2, data), PREC);
        assertEquals(0.666666666667, similarity.similarity(u1, u3, data), PREC);
        assertEquals(0.542450802897, similarity.similarity(u3, u2, data), PREC);
    }


}