package recsys.domain;

import recsys.core.Data;
import recsys.core.SimilarityFunction;

public class UserSimilarity implements SimilarityFunction<Integer> {
    @Override
    public double similarity(Integer u1, Integer u2, Data<Integer, ?> data) {
        return 0;
    }
}
