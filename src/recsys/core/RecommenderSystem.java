package recsys.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
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
        this.similarity = similarity;
    }

    public double predictRating(User user, Item item) {
        Integer rating = data.getRating(user, item);
        if (rating != null) {
        	// User has already rated this item, no prediction needed
        	return rating;
        }
        
        // Find all users who have rated the item
        Collection<User> hasRated = data.getItemRatings(item).keySet();
        
        int k = 5; // TODO Used further down, might want to set this variable earlier/elsewhere?
        // Find the nearest neighbors among all the users who have rated the item
        Map<User, Double> kNN = findKNN(user, hasRated, k); 
        
        // user rates the item according to the mean of the k nearest neighbors
        int sum = 0;
        for (Entry<User, Double> pair : kNN.entrySet()) {
            sum += data.getRating(pair.getKey(), item);
            // System.out.println("sum = " + sum + ", likeness = " + pair.getValue()); // TODO: remove
        }
        
        return (float) sum / k + 1;
    }

    public Collection<Item> getRecommendedItems(User user, int numberOfItems) {
        // TODO: Implement
        return new LinkedList<>();
    }
    
    /**
     * Finds the k nearest neighbors of user in users (will include user if user is in users).
     * @param user The user to find the nearest neighbors of
     * @param users List of users among the nearest neighbors are to be found
     * @param k Number of nearest neighbors to find
     * @return Map of nearest neighbors and their similarity with user
     */
    private Map<User, Double> findKNN(User user, Collection<User> users, int k) {
    	Map<User, Double> kNN = new HashMap<User, Double>(); 	// map of users and similarity to user
        double minSim = 2; 		// minimum similarity of user and its k nearest neighbors
        for (User u : users) {
        	double sim = similarity.similarity(user, u, data);
        	if (kNN.size() < k) {
        		// Fill up map
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
}
