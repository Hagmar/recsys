package recsys.domain;

import recsys.core.BaseSimilarity;

import java.io.Serializable;
import java.util.Map;

/**
 * Computes the cosine similarity between two users.
 */
public class UserSimilarity extends BaseSimilarity<User> {

    @Override
    public <Item> double similarity(User u1, User u2, Map<Item, Integer> ratings1, Map<Item, Integer> ratings2) {
        return cosineSimilarity(ratings1, ratings2);
    }

    private double cosineSimilarity(Map<?, Integer> ratings1, Map<?, Integer> ratings2) {
        if (ratings1 == null || ratings2 == null)
            return 0;

        // Set ratings1 as the smaller set
        if (ratings2.size() < ratings1.size()) {
            Map<?, Integer> smaller = ratings2;
            ratings2 = ratings1;
            ratings1 = smaller;
        }

        // Calculate similarity: cosine similarity = dotProduct(u1, u2) / (||u1|| * ||u2||)
        int scalarProduct = 0;
        int length1 = 0, length2 = 0;   // ||u1||, ||u2|| length of rating vectors u1 and u2 (squared)
        for (Map.Entry<?, Integer> u1 : ratings1.entrySet()) {
            Integer rating2 = ratings2.get(u1.getKey());
            if (rating2 != null) {
                scalarProduct += u1.getValue() * rating2;
            }

            length1 += Math.pow(u1.getValue(), 2);     // Add to length
        }

        // Calculate length of u2
        for (Map.Entry<?, Integer> u2 : ratings2.entrySet()) {
            length2 += Math.pow(u2.getValue(), 2);
        }

        // Not defined for zero lengths
        if (length1 == 0 || length2 == 0)
            return 0;

        return scalarProduct / (Math.sqrt(length1) * Math.sqrt(length2));
    }
}
