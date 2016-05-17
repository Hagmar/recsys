package recsys.domain;

import recsys.core.SimilarityFunction;

public class SimilarityFunctionFactory {
    public static SimilarityFunction<User> getFunction() {
        return new UserSimilarity();
    }
}
