package recsys.domain;

import recsys.core.SimilarityFunction;
import recsys.core.ItemSimilarityFunction;

public class SimilarityFunctionFactory {
    public static SimilarityFunction<User> getFunction() {
        return new UserSimilarity();
    }

    public static ItemSimilarityFunction<Movie> getMovieFunction() {
        return new MovieSimilarity();
    }
}
