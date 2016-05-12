package recsys.weka;

import recsys.core.Data;
import recsys.domain.InMemoryData;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Enumeration;

/**
 * Maps Weka {@link weka.core.Instances} to a {@link recsys.core.Data} container.
 */
class InstancesDataMapper {

    public static Data<Integer, Integer> map(Instances instances) {
        InMemoryData data = new InMemoryData();
        Enumeration<Instance> enumeration = instances.enumerateInstances();
        while (enumeration.hasMoreElements()) {
            Instance instance = enumeration.nextElement();
            data.add((int) instance.value(0), (int) instance.value(1), (int) instance.value(2));
        }
        return data;
    }

}
