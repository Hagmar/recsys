package recsys.weka;

import recsys.core.Data;
import recsys.core.RecommenderSystem;
import recsys.core.SimilarityFunction;
import recsys.core.ItemSimilarityFunction;
import recsys.domain.Movie;
import recsys.domain.SimilarityFunctionFactory;
import recsys.domain.User;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Weka classifier that calls the recommender system.
 */
class RecommenderClassifier extends Classifier {

    private RecommenderSystem<User, Movie> system;

    @Override
    public void buildClassifier(Instances instances) throws Exception {
        SimilarityFunction<User> similarity = SimilarityFunctionFactory.getFunction();
        ItemSimilarityFunction<Movie> itemSimilarity = SimilarityFunctionFactory.getMovieFunction();
        Data<User, Movie> data = InstanceMapper.map(instances);
        system = new RecommenderSystem<>(data, similarity, itemSimilarity);
    }

    @Override
    public double classifyInstance(Instance instance) throws Exception {
        // Attribute 1=item
        return system.predictRating(InstanceMapper.mapUser(instance), InstanceMapper.mapMovie(instance));
    }
}
