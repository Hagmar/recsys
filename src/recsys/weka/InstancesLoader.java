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
        query.setQuery("select * from ratings");

        Instances instances = query.retrieveInstances();

        // Set attribute names
        instances.renameAttribute(0, "user");
        instances.renameAttribute(1, "movie");
        instances.renameAttribute(2, "rating");
        instances.deleteAttributeAt(3);     // timestamp

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
        instances.deleteAttributeAt(3);     // timestamp

        // Set class
        instances.setClass(instances.attribute("rating"));

        return instances;
    }

}
