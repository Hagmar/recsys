package recsys.weka;

import recsys.core.Configuration;
import recsys.core.Data;
import recsys.domain.InMemoryData;
import recsys.domain.Movie;
import recsys.domain.User;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps Weka {@link weka.core.Instances} to a {@link recsys.core.Data} container.
 */
class InstanceMapper {

    public static Data<User, Movie> map(Instances instances) {
        InMemoryData data = new InMemoryData();
        Enumeration<Instance> enumeration = instances.enumerateInstances();
        Map<Integer, Movie> movieMap = new HashMap<>();
        Movie instanceMovie;
        while (enumeration.hasMoreElements()) {
            Instance instance = enumeration.nextElement();

            int movieId = (int) instance.value(1);
            instanceMovie = movieMap.get(movieId);
            if (instanceMovie == null) {
                instanceMovie = movieMap.put(movieId, mapMovie(instance));
            }

            data.add(mapUser(instance), instanceMovie, (int) instance.value(2));
        }
        return data;
    }

    public static User mapUser(Instance instance) {
        // Instance attributes are [0=user, 1=item, 2=rating, 3=age, 4=gender, 5=zipcode, 6=occupation]
        return new User((int) instance.value(0), instance.value(4) > 0, (int) instance.value(3),
                (int) instance.value(5), (int) instance.value(6));
    }

    public static Movie mapMovie(Instance instance) {
        // Instance attributes are [1=movie, 7=releasedate, 8-26=genres 1-19]
        boolean[] genres = new boolean[Configuration.NUMBER_OF_GENRES];
        for (int i = 0; i < Configuration.NUMBER_OF_GENRES; i++) {
            genres[i] = instance.value(i+7) > 0;
        }
        return new Movie((int) instance.value(1), (int) instance.value(7), genres);
    }
}
