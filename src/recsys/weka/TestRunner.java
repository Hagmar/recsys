package recsys.weka;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;

import java.util.Random;

/**
 * Main startup class for evaluating the recommender system using Weka.
 */
public class TestRunner {

    private static final int CROSS_VALIDATION_FOLDS = 10;

    private Instances data;

    public void run() throws Exception {
        System.out.println("Starting tests");

        data = InstancesLoader.get();

        crossValidation(new LinearRegression());
        crossValidation(new RecommenderClassifier());
    }

    private void crossValidation(Classifier cls) throws Exception {
        String name = cls.getClass().getSimpleName();
        System.out.println(name + ": building instances");
        long startTime = System.nanoTime();

        Evaluation eval = new Evaluation(data);
        cls.buildClassifier(data);
        Random rand = new Random(1);        // Using seed = 1
        long buildTime = System.nanoTime() - startTime;

        System.out.println(name + ": " + CROSS_VALIDATION_FOLDS + "-fold cross-validation");
        eval.crossValidateModel(cls, data, CROSS_VALIDATION_FOLDS, rand);
        System.out.println(eval.toSummaryString());
        long runTime = System.nanoTime() - startTime - buildTime;
        System.out.println(name + ": build time: " + (int)(buildTime / 1e6) + "ms, run time: " +
                (int)(runTime / 1e6) + "ms");
    }

    public static void main(String[] args) {
        try {
            new TestRunner().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
