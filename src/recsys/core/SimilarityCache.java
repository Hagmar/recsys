package recsys.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Decorator for similarity functions that caches already computed similarities.
 */
class SimilarityCache<User> extends BaseSimilarity<User> {
    private final Map<Pair<User, User>, Double> cache = new HashMap<>();
    private final SimilarityFunction<User> similarity;

    public SimilarityCache(SimilarityFunction<User> similarity) {
        this.similarity = similarity;
    }

    @Override
    public <Item> double similarity(User u1, User u2, Map<Item, Double> u1Ratings, Map<Item, Double> u2Ratings) {
        Pair<User, User> pair = new Pair<>(u1, u2);
        Double result = cache.get(pair);
        if (result == null) {
            result = similarity.similarity(u1, u2, u1Ratings, u2Ratings);
            cache.put(pair, result);
        }
        return result;
    }

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
