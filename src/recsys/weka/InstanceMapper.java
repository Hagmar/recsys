package recsys.weka;

import recsys.core.Data;
import recsys.domain.InMemoryData;
import recsys.domain.User;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Enumeration;

/**
 * Maps Weka {@link weka.core.Instances} to a {@link recsys.core.Data} container.
 */
class InstanceMapper {

    public static Data<User, Integer> map(Instances instances) {
        InMemoryData data = new InMemoryData();
        Enumeration<Instance> enumeration = instances.enumerateInstances();
        while (enumeration.hasMoreElements()) {
            Instance instance = enumeration.nextElement();
            data.add(mapUser(instance), (int) instance.value(1), (int) instance.value(2));
        }
        return data;
    }

    public static User mapUser(Instance instance) {
        // Instance attributes are [0=user, 1=item, 2=rating, 3=age, 4=gender]
        return new User((int) instance.value(0), instance.value(4) > 0, (int) instance.value(3));
    }
}
