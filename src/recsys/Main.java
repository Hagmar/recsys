package recsys;

import recsys.core.Data;
import recsys.core.RecommenderSystem;
import recsys.core.SimilarityFunction;

public class Main {

    public static void main(String[] args) {
        // TODO: Parse the data files
        DataContainer data = new DataContainer();
        // data.parse();

        // Instantiate similarity function
        SimilarityFunction<Integer> similarity = new UserSimilarity();

        // Create recommender system
        RecommenderSystem<Integer, Integer> system = new RecommenderSystem<>(data, similarity);

        // Run command line interface
        CLI commandLineInterface = new CLI(system);
        commandLineInterface.run();
    }
}
