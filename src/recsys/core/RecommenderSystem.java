package recsys.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

/**
 * Calculates predictions of user ratings for items and uses them to recommend items for particular users.
 * @param <User> The identifier of a user.
 * @param <Item> The identifier of an item.
 */
public class RecommenderSystem<User, Item> implements Serializable {

    private final Data<User, Item> data;
    private final SimilarityFunction<User> similarity;
    private final Random random = new Random();

    public RecommenderSystem(Data<User, Item> data, SimilarityFunction<User> similarity) {
        this.data = data;
        this.similarity = similarity;
    }

    public double predictRating(User user, Item item) {
        // TODO: Implement - Use similarity and data fields to predict rating
        Integer rating = data.getRating(user, item);
        // Find the kNN's (using the existing similarity function), user rates the item according to the mean of the kNN
        return rating != null ? rating : random.nextInt(5) + 1;

    }

    public Collection<Item> getRecommendedItems(User user, int numberOfItems) {
        // TODO: Implement
        return new LinkedList<>();
    }
}
