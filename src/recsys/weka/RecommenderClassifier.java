package recsys.weka;

import recsys.core.Data;
import recsys.core.RecommenderSystem;
import recsys.core.SimilarityFunction;
import recsys.domain.SimilarityFunctionFactory;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Weka classifier that calls the recommender system.
 */
class RecommenderClassifier extends Classifier {

    private RecommenderSystem<Integer, Integer> system;

    @Override
    public void buildClassifier(Instances instances) throws Exception {
        SimilarityFunction<Integer> similarity = SimilarityFunctionFactory.getFunction();
        Data<Integer, Integer> data = InstancesDataMapper.map(instances);
        system = new RecommenderSystem<>(data, similarity);
    }

    @Override
    public double classifyInstance(Instance instance) throws Exception {
        // Instance attributes are [0=user, 1=item, 2=rating]
        return system.predictRating((int) instance.value(0), (int) instance.value(1));
    }
}
