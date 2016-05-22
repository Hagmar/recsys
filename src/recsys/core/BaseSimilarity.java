package recsys.core;
import java.io.Serializable;
import java.util.Map;

public abstract class BaseSimilarity<User> implements SimilarityFunction<User>, Serializable {
    @Override
    public <Item> double similarity(User user1, User user2, Data<User, Item> data) {
        Map<Item, Double> ratings1 = data.getRatings(user1);
        Map<Item, Double> ratings2 = data.getRatings(user2);

        return similarity(user1, user2, ratings1, ratings2);
    }
}
