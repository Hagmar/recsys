package recsys.domain;

import recsys.core.BaseSimilarity;
import recsys.core.Configuration;

import java.util.Map;

/**
 * Computes the cosine similarity between two users.
 */
public class UserSimilarity extends BaseSimilarity<User> {

    private static final double DEMOGRAPHIC_WEIGHT = Configuration.USER_DEMOGRAPHIC_SIMILARITY_WEIGHT;
    private static final double RATINGS_WEIGHT = 1 - DEMOGRAPHIC_WEIGHT;

    @Override
    public <Item> double similarity(User u1, User u2, Map<Item, Double> ratings1, Map<Item, Double> ratings2) {
        return RATINGS_WEIGHT * ratingSimilarity(ratings1, ratings2) +
                DEMOGRAPHIC_WEIGHT * demographicSimilarity(u1, u2);
    }

    private double ratingSimilarity(Map<?, Double> ratings1, Map<?, Double> ratings2) {
        if (ratings1 == null || ratings2 == null)
            return 0;

        // Set ratings1 as the smaller set (to only iterate over the smaller list later)
        if (ratings2.size() < ratings1.size()) {
            Map<?, Double> smaller = ratings2;
            ratings2 = ratings1;
            ratings1 = smaller;
        }

        // Calculate with Pearson similarity
        double nominator = 0;
        double denominator1 = 0, denominator2 = 0;
        int ratedByBoth = 0;
        for (Map.Entry<?, Double> u1 : ratings1.entrySet()) {
            Double rating2 = ratings2.get(u1.getKey());

            // Only consider items that are rated by both users
            if (rating2 != null) {
                double rating1 = u1.getValue();
                ratedByBoth++;
                nominator += rating1 * rating2;
                denominator1 += Math.pow(rating1, 2);
                denominator2 += Math.pow(rating2, 2);
            }
        }

        double smoothing = Math.min(1, ratedByBoth / 50.0);
        double rating = nominator / (Math.sqrt(denominator1) * Math.sqrt(denominator2));
        return smoothing * rating;
    }

    private double demographicSimilarity(User u1, User u2) {
        if (u1 == null || u2 == null)
            return 0;

        double genderSimilarity = u1.getGender() == u2.getGender() ? 0.1 : 0;

        // Admissible measurement of age similarity?
        double ageSimilarity = Math.abs(u1.getAge() - u2.getAge());
        ageSimilarity = 1 - Math.min(1, ageSimilarity/Math.min(u1.getAge(), u2.getAge()));

        double zipcodeSimilarity;
        if (u1.getZipcode() == 0) {
            zipcodeSimilarity = 0;
        } else {
            // Quadratic function giving 0 similarity to 100.000 difference in zip code
            zipcodeSimilarity = Math.abs(u1.getZipcode() - u2.getZipcode());
            zipcodeSimilarity = 1-(Math.pow(zipcodeSimilarity, 2)/10000000000.0);
            zipcodeSimilarity = Math.max(zipcodeSimilarity, 0);
        }

        double occupationSimilarity;
        if (u1.getOccupation() != 0) {
            occupationSimilarity = u1.getOccupation() == u2.getOccupation() ? 0.1 : 0;
        } else {
            occupationSimilarity = 0;
        }

        double similarity = .4 * genderSimilarity + 0.4 * ageSimilarity + 0.2 * zipcodeSimilarity + 0 * occupationSimilarity;

        return similarity;
    }
}
