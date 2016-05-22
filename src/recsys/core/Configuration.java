package recsys.core;

/**
 * Constants for tweaking the recommender system.
 */
class Configuration {
    /**
     * The number of nearest neighbors (k) to use for prediction.
     */
    public static final int NEAREST_NEIGHBORS_NUMBER = 5;
    /**
     * Use weighted average (based on user similarity) instead of the mean
     * of the similar users' ratings.
     */
    public static final boolean WEIGHTED_AVERAGE = true;
    /**
     * Sets the smoothing mode.
     */
    public static final Smoothing SMOOTHING = Smoothing.NONE;


    public enum Smoothing {
        /** Smoothing disabled */
        NONE,
        /** One user with all ratings set to 3 is added. */
        ALL_3
    }
}
