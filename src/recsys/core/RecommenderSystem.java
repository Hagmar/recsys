package recsys.core;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Calculates predictions of user ratings for items and uses them to recommend items for particular users.
 * @param <User> The identifier of a user.
 * @param <Item> The identifier of an item.
 */
public class RecommenderSystem<User, Item> {

    private final Data<User, Item> data;
    private final SimilarityFunction<User> similarity;

    public RecommenderSystem(Data<User, Item> data, SimilarityFunction<User> similarity) {
        this.data = data;
        this.similarity = similarity;
    }

    public double predictRating(User user, Item item) {
        // TODO: implement
        // Use similarity and data fields to predict rating
        return 0;
    }

    public Collection<Item> getRecommendedItems(User user, int numberOfItems) {
        // TODO: implement
        return new LinkedList<>();
    }
}
