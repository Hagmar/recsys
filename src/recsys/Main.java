package recsys;

import recsys.core.Data;
import recsys.core.RecommenderSystem;
import recsys.core.SimilarityFunction;
import recsys.domain.InMemoryData;
import recsys.domain.UserSimilarity;

public class Main {

    public static void main(String[] args) {
        // Load data container - parse the data files into memory
        Data<Integer, Integer> data = new InMemoryData("ml-100k/u.data");

        // Instantiate the similarity function to use
        SimilarityFunction<Integer> similarity = new UserSimilarity();

        // Create recommender system
        RecommenderSystem<Integer, Integer> system = new RecommenderSystem<>(data, similarity);

        // Run command line interface
        CLI commandLineInterface = new CLI(system);
        commandLineInterface.run();
    }
}
