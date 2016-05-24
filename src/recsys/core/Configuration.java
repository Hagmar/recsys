package recsys.core;

/**
 * Constants for tweaking the recommender system.
 */
public class Configuration {
    /**
     * The number of nearest neighbors (k) to use for prediction.
     */
    public static final int NEAREST_NEIGHBORS_NUMBER = 30;
    /**
     * Use weighted average (based on user similarity) instead of the mean
     * of the similar users' ratings.
     */
    public static final boolean WEIGHTED_AVERAGE = true;
    /**
     * Sets the smoothing mode.
     */
    public static final Smoothing SMOOTHING = Smoothing.ITEM_AVERAGE;
    /**
     * The default rating average for items that have no ratings (probably 0 or 3).
     */
    public static final double DEFAULT_AVERAGE = 3;
    /**
     * Determines how much to weight demographics for user similarity. 1 minus this
     * value is the weight for ratings similarity.
     */
    public static final double USER_DEMOGRAPHIC_SIMILARITY_WEIGHT = .1;


    public enum Smoothing {
        /** Smoothing disabled. */
        NONE,
        /** One user that has rated all items with a 3. */
        ALL_3,
        /** One user that has rated all items with each item's average rating. */
        ITEM_AVERAGE
    }
}
