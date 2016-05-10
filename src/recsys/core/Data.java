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
     * @return A map indexed with item and key as rating.
     */
    Map<Item, Integer> getRatings(User user);

    /**
     * Returns the rating by a user for an item.
     * @param user The user who has rated the item.
     * @param item The item that has been rated.
     * @return The rating or null if no rating exists.
     */
    Integer getRating(User user, Item item);

    /**
     * Returns all users in the dataset.
     * @return A collection of users.
     */
    Collection<User> getUsers();
}
