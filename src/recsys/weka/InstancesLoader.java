package recsys.weka;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils;
import weka.core.converters.Loader;
import weka.experiment.InstanceQuery;

import java.io.File;

/**
 * Loads the {@link weka.core.Instances} from file or database.
 */
class InstancesLoader {

    public static Instances get() throws Exception {
        return getFromFile();
    }

    private static Instances getFromDatabase() throws Exception {
        InstanceQuery query = new InstanceQuery();      // TODO: implement when database is ready
        query.setQuery("select * from whatsoever");
        return query.retrieveInstances();
    }

    private static Instances getFromFile() throws Exception {
        // Read all the instances in the file (ARFF, CSV, XRFF, ...)
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File("ml-100k/u.data"));
        loader.setOptions(weka.core.Utils.splitOptions("-F '\t'"));     // Set CSV delimiter

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
