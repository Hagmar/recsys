package recsys;

import recsys.core.Data;
import recsys.core.RecommenderSystem;
import recsys.core.SimilarityFunction;
import recsys.domain.*;

public class Main {

    public static void main(String[] args) {
        // Load data container - parse the data files into memory
        Data<User, Integer> data = new InMemoryData("ml-100k/ml-100k/u.data");
        //Data<User, Integer> data = new SqliteData();

        // Instantiate the similarity function to use
        SimilarityFunction<User> similarity = SimilarityFunctionFactory.getFunction();

        // Create recommender system
        RecommenderSystem<User, Integer> system = new RecommenderSystem<>(data, similarity);

        // Run command line interface
        CLI commandLineInterface = new CLI(system);
        commandLineInterface.run();
    }
}
