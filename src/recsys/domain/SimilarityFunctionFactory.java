package recsys.domain;

import recsys.core.SimilarityFunction;

public class SimilarityFunctionFactory {
    public static SimilarityFunction<Integer> getFunction() {
        return new UserSimilarity();
    }
}
