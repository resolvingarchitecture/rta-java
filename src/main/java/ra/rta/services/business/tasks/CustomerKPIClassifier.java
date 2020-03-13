package ra.rta.services.business.tasks;

import com.google.common.cache.LoadingCache;
import ra.rta.models.ClassifiableEvent;
import ra.rta.models.Classifier;
import ra.rta.models.Customer;
import ra.rta.models.KPI;

import java.util.LinkedHashSet;

/**
 * {
 */
public class CustomerKPIClassifier implements Classifier {

    @Override
    public void classify(ClassifiableEvent event, LoadingCache<String, LinkedHashSet<KPI>> exactMatchTermcodeCache) {
        Customer customer = (Customer)event.getEntity();
        // TODO: Implement
    }
}
