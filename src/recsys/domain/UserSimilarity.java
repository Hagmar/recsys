package recsys.domain;

import recsys.core.BaseSimilarity;

import java.io.Serializable;
import java.util.Map;

import static java.lang.Math.min;

/**
 * Computes the cosine similarity between two users.
 */
public class UserSimilarity extends BaseSimilarity<User> {

    @Override
    public <Item> double similarity(User u1, User u2, Map<Item, Double> ratings1, Map<Item, Double> ratings2) {
        // TODO Use demographic similarity
        double demoSimilarity = demographicSimilarity(u1, u2);
        return cosineSimilarity(ratings1, ratings2);
    }

    private double cosineSimilarity(Map<?, Double> ratings1, Map<?, Double> ratings2) {
        if (ratings1 == null || ratings2 == null)
            return 0;

        // Set ratings1 as the smaller set
        if (ratings2.size() < ratings1.size()) {
            Map<?, Double> smaller = ratings2;
            ratings2 = ratings1;
            ratings1 = smaller;
        }

        // Calculate similarity: cosine similarity = dotProduct(u1, u2) / (||u1|| * ||u2||)
        int scalarProduct = 0;
        int length1 = 0, length2 = 0;   // ||u1||, ||u2|| length of rating vectors u1 and u2 (squared)
        for (Map.Entry<?, Double> u1 : ratings1.entrySet()) {
            Double rating2 = ratings2.get(u1.getKey());
            if (rating2 != null) {
                scalarProduct += u1.getValue() * rating2;
            }

            length1 += Math.pow(u1.getValue(), 2);     // Add to length
        }

        // Calculate length of u2
        for (Map.Entry<?, Double> u2 : ratings2.entrySet()) {
            length2 += Math.pow(u2.getValue(), 2);
        }

        // Not defined for zero lengths
        if (length1 == 0 || length2 == 0)
            return 0;

        return scalarProduct / (Math.sqrt(length1) * Math.sqrt(length2));
    }

    private double demographicSimilarity(User u1, User u2) {
        if (u1 == null || u2 == null)
            return 0;

        double genderSimilarity = u1.getGender() == u2.getGender() ? 0.1 : 0;

        // Admissible measurement of age similarity?
        double ageSimilarity = Math.abs(u1.getAge() - u2.getAge());
        ageSimilarity = 1 - min(1, ageSimilarity/min(u1.getAge(), u2.getAge()));

        double zipcodeSimilarity;
        if (u1.getZipcode() == 0) {
            zipcodeSimilarity = 0;
        } else {
            // 0.0002 gives a reasonable similaritp for zip codes in the USA
            zipcodeSimilarity = Math.abs(u1.getZipcode() - u2.getZipcode());
            zipcodeSimilarity = Math.exp(-0.0002 * zipcodeSimilarity);
        }

        double occupationSimilarity;
        if (u1.getOccupation() != 0) {
            occupationSimilarity = u1.getOccupation() == u2.getOccupation() ? 0.1 : 0;
        } else {
            occupationSimilarity = 0;
        }

        // Weight somehow?
        double similarity = genderSimilarity + 0.6*ageSimilarity + 0.2*zipcodeSimilarity + occupationSimilarity;

        return similarity;
    }
}
