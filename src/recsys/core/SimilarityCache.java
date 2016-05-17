package recsys.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Anton Jansson.
 */
class SimilarityCache<User> implements SimilarityFunction<User>, Serializable {
    private final Map<Pair<User, User>, Double> cache = new HashMap<>();
    private final SimilarityFunction<User> similarity;

    public SimilarityCache(SimilarityFunction<User> similarity) {
        this.similarity = similarity;
    }

    @Override
    public double similarity(User u1, User u2, Data<User, ?> data) {
        Pair<User, User> pair = new Pair<>(u1, u2);
        Double result = cache.get(pair);
        if (result == null) {
            result = similarity.similarity(u1, u2, data);
            cache.put(pair, result);
        }
        return result;
    }

    /**
     * Container to ease passing around a tuple of two objects. This object provides a sensible
     * implementation of equals(), returning true if equals() is true on each of the contained
     * objects.
     */
    private static class Pair<F, S> {
        final F first;
        final S second;

        Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Pair)) {
                return false;
            }
            Pair<?, ?> p = (Pair<?, ?>) o;
            return Objects.equals(p.first, first) && Objects.equals(p.second, second) ||
                    Objects.equals(p.first, second) && Objects.equals(p.second, first);
        }

        @Override
        public int hashCode() {
            return (first == null ? 0 : first.hashCode()) * (second == null ? 0 : second.hashCode());
        }
    }
}
