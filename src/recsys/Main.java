package recsys;

import recsys.core.Data;
import recsys.core.ItemSimilarityFunction;
import recsys.core.RecommenderSystem;
import recsys.core.SimilarityFunction;
import recsys.domain.*;

public class Main {

    public static void main(String[] args) {
        // Load data container - parse the data files into memory
        //Data<User, Integer> data = new InMemoryData("ml-100k/ml-100k/u.data");
        Data<User, Movie> data = new SqliteData();

        // Instantiate the similarity function to use
        SimilarityFunction<User> similarity = SimilarityFunctionFactory.getFunction();

        // Instantiate the item similarity function to use
        ItemSimilarityFunction<Movie> itemSimilarity = SimilarityFunctionFactory.getMovieFunction();

        // Create recommender system
        RecommenderSystem<User, Movie> system = new RecommenderSystem<>(data, similarity, itemSimilarity);

        // Run command line interface
        CLI commandLineInterface = new CLI(system);
        commandLineInterface.run();
    }
}
