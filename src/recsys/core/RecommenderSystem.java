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

    private final Data<User, Item> data;
    private final SimilarityFunction<User> similarity;

    public RecommenderSystem(Data<User, Item> data, SimilarityFunction<User> similarity) {
        this.data = data;
        this.similarity = new SimilarityCache<>(similarity);
    }

    public double predictRating(User user, Item item) {
        Map<Item, Integer> userRatings = data.getRatings(user);
        if (userRatings != null) {
            Integer rating = userRatings.get(item);
            if (rating != null) {
                return rating;  // User has already rated this item, no prediction needed
            }
        } else {
            userRatings = new HashMap<>();
        }
        
        // Find all users who have rated the item
        Map<User, Map<Item, Integer>> otherUsers = data.getUserRatingsByItem(item);

        if (Configuration.SMOOTHING != Configuration.Smoothing.NONE) {
            otherUsers.put(null, getSmoothingRatings(userRatings.keySet(), item));
        }

        // Find the nearest neighbors among all the users who have rated the item
        Map<User, Double> kNN = findKNN(user, userRatings, otherUsers,
                Configuration.NEAREST_NEIGHBORS_NUMBER);
        
        // User rates the item according to the [weighted] average of the k nearest neighbors
        float sum = 0, denominator = 0;
        for (Entry<User, Double> pair : kNN.entrySet()) {
            int rating = otherUsers.get(pair.getKey()).get(item);
            if (Configuration.WEIGHTED_AVERAGE) {
                denominator += pair.getValue();
                sum += pair.getValue() * rating;
            } else {
                sum += rating;
            }
        }

        if (Configuration.WEIGHTED_AVERAGE) {
            return sum / denominator;
        } else {
            return sum / Configuration.NEAREST_NEIGHBORS_NUMBER;
        }
    }

    public Collection<Item> getRecommendedItems(User user, int numberOfItems) {
        // TODO: Implement
        return new LinkedList<>();
    }
    
    /**
     * Finds the k nearest neighbors of user in users (will include user if user is in users).
     * @param user The user to find the nearest neighbors of
     * @param userRatings The ratings of the user to find nearest neighbors of
     * @param otherUsers List of users among the nearest neighbors are to be found, with their ratings.
     * @param k Number of nearest neighbors to find
     * @return Map of nearest neighbors and their similarity with user
     */
    private Map<User, Double> findKNN(User user, Map<Item, Integer> userRatings,
                                      Map<User, Map<Item, Integer>> otherUsers, int k) {
    	Map<User, Double> kNN = new HashMap<User, Double>(); 	// mapUser of users and similarity to user
        double minSim = 2; 		// minimum similarity of user and its k nearest neighbors
        for (Entry<User, Map<Item, Integer>> entry : otherUsers.entrySet()) {
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
     * Return ratings used for smoothing.
     * @param userRatedItems Items to get smooth ratings for (items rated by the user to predict
     *                       for is enough sincethese are the only ratings compared in similarity
     *                       function).
     * @param queryItem The item which rating should be predicted.
     * @return Ratings for the items.
     */
    private Map<Item, Integer> getSmoothingRatings(Collection<Item> userRatedItems, Item queryItem) {
        Collection<Item> items = new ArrayList<>(userRatedItems.size() + 1);
        items.addAll(userRatedItems);
        items.add(queryItem);

        Map<Item, Integer> smoothingRatings = new HashMap<>();
        for (Item item : items) {
            switch (Configuration.SMOOTHING) {
                case ALL_3:
                    smoothingRatings.put(item, 3);
                    break;
            }
        }
        return smoothingRatings;
    }
}
