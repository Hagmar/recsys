package recsys.weka;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.util.Random;

/**
 * Main startup class for evaluating the recommender system using Weka.
 */
public class TestRunner {

    private static final int CROSS_VALIDATION_FOLDS = 10;

    public void run() throws Exception {
        System.out.println("Starting tests - load data instances...");

        Instances data = InstancesLoader.get();
        Classifier cls = new RecommenderClassifier();
        cls.buildClassifier(data);

        Evaluation eval = new Evaluation(data);
        Random rand = new Random(1);        // Using seed = 1

        System.out.println("Running cross-validation with " + CROSS_VALIDATION_FOLDS + " folds...");
        eval.crossValidateModel(cls, data, CROSS_VALIDATION_FOLDS, rand);
        System.out.println(eval.toSummaryString());
    }

    public static void main(String[] args) {
        try {
            new TestRunner().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
