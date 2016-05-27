package recsys.weka;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.experiment.InstanceQuery;

import java.io.File;

/**
 * Loads the {@link weka.core.Instances} from file or database.
 */
class InstancesLoader {

    public static Instances get() throws Exception {
        return getFromDatabase();
    }

    private static Instances getFromDatabase() throws Exception {
        InstanceQuery query = new InstanceQuery();
        query.setQuery("SELECT user, movie, rating, age, gender, zipcode, occupation " +
                "releasedate, g_1, g_2, g_3, g_4, g_5, g_6, g_7, g_8, g_9, g_10, g_11, g_12," +
                "g_13, g_14, g_15, g_16, g_17, g_18, g_19 " +
                "FROM ratings JOIN users ON user = users.id JOIN movies ON movie = movies.id");

        Instances instances = query.retrieveInstances();

        // Set attribute names
        instances.renameAttribute(0, "user");
        instances.renameAttribute(1, "movie");
        instances.renameAttribute(2, "rating");

        // Set class
        instances.setClass(instances.attribute("rating"));

        return instances;
    }

    private static Instances getFromFile() throws Exception {
        // Read all the instances in the file (ARFF, CSV, XRFF, ...)
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File("ml-100k/u.data"));
        String options = "-H -F '\t'";         // Set CSV delimiter and no header
        loader.setOptions(weka.core.Utils.splitOptions(options));

        Instances instances = loader.getDataSet();

        // Set attribute names
        instances.renameAttribute(0, "user");
        instances.renameAttribute(1, "movie");
        instances.renameAttribute(2, "rating");

        // Set class
        instances.setClass(instances.attribute("rating"));

        return instances;
    }

}
