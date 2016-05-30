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
     * The number of nearest neighbors (k) to use for content-based prediction on
     * movie features.
     */
    public static final int MOVIE_NEAREST_NEIGHBORS_NUMBER = 10;
    /**
     * Use weighted average (based on user similarity) instead of the mean
     * of the similar users' ratings.
     */
    public static final boolean WEIGHTED_AVERAGE = true;
    /**
     * Sets the smoothing mode.
     */
    public static final Smoothing SMOOTHING = Smoothing.NONE;
    /**
     * The default rating average for items that have no ratings (probably 0 or 3).
     */
    public static final double DEFAULT_AVERAGE = 3;
    /**
     * Determines how much to weight collaborative filtering rating prediction.
     * 1 minus this value is the weight for the content-based prediction.
     */
    public static final double COLLABORATIVE_RATING_WEIGHT = 0.7;
    /**
     * Determines how much to weight demographics for user similarity. 1 minus this
     * value is the weight for ratings similarity.
     */
    public static final double USER_DEMOGRAPHIC_SIMILARITY_WEIGHT = .1;
    /**
     * Determines how much to weight genre similarity when comparing movies. 1 minus
     * this value is the weight for year similarity.
     */
    public static final double MOVIE_GENRE_SIMILARITY_WEIGHT = 0.8;
    /**
     * The number of genres that exist. Needed to determine the size of the genre
     * array for movies.
     */
    public static final int NUMBER_OF_GENRES = 19;

    public enum Smoothing {
        /** Smoothing disabled. */
        NONE,
        /** One user that has rated all items with a 3. */
        ALL_3,
        /** One user that has rated all items with each item's average rating. */
        ITEM_AVERAGE
    }
}
