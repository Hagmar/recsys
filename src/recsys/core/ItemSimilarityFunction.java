package recsys.core;

/**
 * Calculates the similarity between two movie.
 * @param <Item> The identifier of a user.
 */
public interface ItemSimilarityFunction<Item> {
    /**
     * Returns a similarity between [0,1] determining the similarity between the items.
     * @param m1 Item 1.
     * @param m2 Item 2.
     * @return Returns a floating number [0,1] where 1 means items are exactly alike and 0
     *      means that there are no similarities.
     */
    double similarity(Item m1, Item m2);
}
