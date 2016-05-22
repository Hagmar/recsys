package recsys.core;

import java.util.Collection;
import java.util.Map;

/**
 * Container for the data of the recommender system. This allows the recommender system to
 * look up ratings for users and items, without knowing the underlying representation of
 * the data.
 */
public interface Data<User, Item> {
    /**
     * Returns ratings for all items that the user has rated.
     * @param user The user who has rated the items.
     * @return A mapUser indexed with item and key as rating.
     */
    Map<Item, Double> getRatings(User user);

    /**
     * Returns the rating by a user for an item.
     * @param user The user who has rated the item.
     * @param item The item that has been rated.
     * @return The rating or null if no rating exists.
     */
    Double getRating(User user, Item item);

    /**
     * Returns ratings from all users who have rated a specific item.
     * @param item The item to get ratings for.
     * @return A map of user as key and the user's rating of specified item as value.
     */
    Map<User, Map<Item, Double>> getUserRatingsByItem(Item item);

    /**
     * Returns the average rating for each item in the parameter list.
     * @param items The items to get average rating for.
     * @return A map of items with their average rating.
     */
    Map<Item, Double> getAverageRatings(Collection<Item> items);
}
