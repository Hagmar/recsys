package recsys.core;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

/**
 * Calculates predictions of user ratings for items and uses them to recommend items for particular users.
 * @param <User> The identifier of a user.
 * @param <Item> The identifier of an item.
 */
public class RecommenderSystem<User, Item> implements Serializable {

    private static final double COLLABORATIVE_WEIGHT = Configuration.COLLABORATIVE_RATING_WEIGHT;
    private static final double CONTENT_WEIGHT = 1 - COLLABORATIVE_WEIGHT;
    private final Data<User, Item> data;
    private final SimilarityFunction<User> similarity;
    private final ItemSimilarityFunction<Item> itemSimilarity;

    public RecommenderSystem(Data<User, Item> data, SimilarityFunction<User> similarity,
                             ItemSimilarityFunction<Item> itemSimilarity) {
        this.data = data;
        this.similarity = new SimilarityCache<>(similarity);
        this.itemSimilarity = itemSimilarity;
    }

    public double predictRating(User user, Item item) {
        Map<Item, Double> userRatings = data.getRatings(user);
        if (userRatings != null) {
            Double rating = userRatings.get(item);
            if (rating != null) {
                return rating;  // User has already rated this item, no prediction needed
            }
        } else {
            userRatings = new HashMap<>();
        }
        double collaborativeRating = collaborativeFilteringPrediction(user, item, userRatings);
        double contentRating = contentBasedPrediction(user, item);
        return COLLABORATIVE_WEIGHT*collaborativeRating + CONTENT_WEIGHT*contentRating;
    }

    private double collaborativeFilteringPrediction(User user, Item item, Map<Item, Double> userRatings){
        // Find all users who have rated the item
        Map<User, Map<Item, Double>> otherUsers = data.getUserRatingsByItem(item);

        if (Configuration.SMOOTHING != Configuration.Smoothing.NONE) {
            otherUsers.put(null, getSmoothingRatings(userRatings.keySet(), item));
        }

        // Find the nearest neighbors among all the users who have rated the item
        Map<User, Double> kNN = findKNN(user, userRatings, otherUsers,
                Configuration.NEAREST_NEIGHBORS_NUMBER);

        // User rates the item according to the [weighted] average of the k nearest neighbors
        float sum = 0, denominator = 0;
        for (Entry<User, Double> pair : kNN.entrySet()) {
            double rating = otherUsers.get(pair.getKey()).get(item);
            if (Configuration.WEIGHTED_AVERAGE) {
                denominator += pair.getValue();
                sum += pair.getValue() * rating;
            } else {
                sum += rating;
            }
        }

        if (Configuration.WEIGHTED_AVERAGE) {
            return denominator > 0 ? sum / denominator : 0;
        } else {
            return sum / Configuration.NEAREST_NEIGHBORS_NUMBER;
        }
    }

    private double contentBasedPrediction(User user, Item item) {
        // Find all movies which the user has rated
        Map<Item, Double> ratedMovies = data.getRatings(user);

        // TODO smoothing
        //if (Configuration.SMOOTHING != Configuration.Smoothing.NONE) {
        //    ratedMovies.put(null, getSmoothingRatings(userRatings.keySet(), item));
        //}

        // Find the nearest neighbors among all the movies which the user has rated
        Map<Item, Double> kNN = findKNNMovies(item, ratedMovies, Configuration.MOVIE_NEAREST_NEIGHBORS_NUMBER);

        // User rates the item according to the [weighted] average of the k nearest neighbors
        float sum = 0, denominator = 0;
        for (Entry<Item, Double> pair : kNN.entrySet()) {
            double rating = ratedMovies.get(pair.getKey());
            if (Configuration.WEIGHTED_AVERAGE) {
                denominator += pair.getValue();
                sum += pair.getValue() * rating;
            } else {
                sum += rating;
            }
        }

        if (Configuration.WEIGHTED_AVERAGE) {
            return denominator > 0 ? sum / denominator : 0;
        } else {
            return sum / Configuration.MOVIE_NEAREST_NEIGHBORS_NUMBER;
        }
    }
    
    /**
     * Finds the k nearest neighbors of user in users (will include user if user is in users).
     * @param user The user to find the nearest neighbors of
     * @param userRatings The ratings of the user to find nearest neighbors of
     * @param otherUsers List of users among the nearest neighbors are to be found, with their ratings.
     * @param k Number of nearest neighbors to find
     * @return Map of nearest neighbors and their similarity with user
     */
    private Map<User, Double> findKNN(User user, Map<Item, Double> userRatings,
                                      Map<User, Map<Item, Double>> otherUsers, int k) {
    	Map<User, Double> kNN = new HashMap<User, Double>(); 	// mapUser of users and similarity to user
        double minSim = 2; 		// minimum similarity of user and its k nearest neighbors
        for (Entry<User, Map<Item, Double>> entry : otherUsers.entrySet()) {
        	User u = entry.getKey();
            double sim = similarity.similarity(user, u, userRatings, entry.getValue());
        	if (kNN.size() < k) {
        		// Fill up mapUser
        		kNN.put(u, sim);
        		if (sim < minSim) {
        			minSim = sim;
        		}
        	} else if(sim > minSim) { 	// add u to users and remove least similar user in users
        		kNN.put(u, sim);
        		Iterator<Entry<User, Double>> it = kNN.entrySet().iterator();
        		double oldMinSim = minSim;
        		minSim = sim;		// sim not necessarily smallest value, minSim is updated in the while loop
        		while (it.hasNext()) {
        			Map.Entry<User, Double> pair = (Entry<User, Double>) it.next();
        			double value = pair.getValue();
        			if (value == oldMinSim && kNN.size() > k) {
        				// remove old neighbor with smallest value of similarity
        				it.remove();
        			} else if (value < minSim) {
        				minSim = value;
        			}
        		}
        	}
        }
    	return kNN;
    }

    /**
     * Finds the k nearest neighbors of movies
     * @param item The movie to find the nearest neighbors of
     * @param ratedMovies The ratings of the user to find nearest neighbors of
     * @param k Number of nearest neighbors to find
     * @return Map of nearest neighbors and their similarity with item
     */
    private Map<Item, Double> findKNNMovies(Item item, Map<Item, Double> ratedMovies, int k) {
        Map<Item, Double> kNN = new HashMap<Item, Double>(); 	// map of movies and similarity to item
        double minSim = 2; 		// minimum similarity of item and its k nearest neighbors
        for (Entry<Item, Double> entry : ratedMovies.entrySet()) {
            Item i = entry.getKey();
            double sim = itemSimilarity.similarity(item, i);
            if (kNN.size() < k) {
                // Fill up map of movies
                kNN.put(i, sim);
                if (sim < minSim) {
                    minSim = sim;
                }
            } else if(sim > minSim) { 	// add i to movies and remove least similar item in movies
                kNN.put(i, sim);
                Iterator<Entry<Item, Double>> it = kNN.entrySet().iterator();
                double oldMinSim = minSim;
                minSim = sim;		// sim not necessarily smallest value, minSim is updated in the while loop
                while (it.hasNext()) {
                    Map.Entry<Item, Double> pair = (Entry<Item, Double>) it.next();
                    double value = pair.getValue();
                    if (value == oldMinSim && kNN.size() > k) {
                        // remove old neighbor with smallest value of similarity
                        it.remove();
                    } else if (value < minSim) {
                        minSim = value;
                    }
                }
            }
        }
        return kNN;
    }

    /**
     * Return ratings used for smoothing.
     * @param userRatedItems Items to get smooth ratings for (items rated by the user to predict
     *                       for is enough sincethese are the only ratings compared in similarity
     *                       function).
     * @param queryItem The item which rating should be predicted.
     * @return Ratings for the items.
     */
    private Map<Item, Double> getSmoothingRatings(Collection<Item> userRatedItems, Item queryItem) {
        Collection<Item> items = new ArrayList<>(userRatedItems.size() + 1);
        items.addAll(userRatedItems);
        items.add(queryItem);

        if (Configuration.SMOOTHING == Configuration.Smoothing.ITEM_AVERAGE)
            return data.getAverageRatings(items);

        Map<Item, Double> smoothingRatings = new HashMap<>();
        for (Item item : items) {
            switch (Configuration.SMOOTHING) {
                case ALL_3:
                    smoothingRatings.put(item, 3.0);
                    break;
            }
        }
        return smoothingRatings;
    }
}
