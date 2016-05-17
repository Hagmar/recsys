package recsys.core;

import java.util.Map;

/**
 * Calculates the similarity between two users.
 * @param <User> The identifier of a user.
 */
public interface SimilarityFunction<User> {
    /**
     * Returns a similarity between [0,1] determining the similarity between the users.
     * @param u1 User 1.
     * @param u2 User 2.
     * @return Returns a floating number [0,1] where 1 means users are exactly alike and 0
     *      means that there are no similarities.
     */
    <Item> double similarity(User u1, User u2, Data<User, Item> data);
    <Item> double similarity(User u1, User u2, Map<Item, Integer> u1Ratings, Map<Item, Integer> u2Ratings);
}
