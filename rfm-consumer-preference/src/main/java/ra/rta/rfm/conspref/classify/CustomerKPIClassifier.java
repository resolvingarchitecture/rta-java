package ra.rta.rfm.conspref.classify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.classify.KPIClassifiable;
import ra.rta.models.Classifier;
import ra.rta.Event;
import ra.rta.classify.KPI;
import ra.rta.rfm.conspref.models.Customer;

import java.util.LinkedHashSet;
import java.util.Map;

/**
 * {
 */
public class CustomerKPIClassifier implements Classifier {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerKPIClassifier.class);

    @Override
    public void classify(KPIClassifiable classifiable, Map<String, LinkedHashSet<? extends KPI>> exactMatchTermcodeCache) {
        Customer customer = null;
        if(classifiable instanceof Event) {
            Event event = (Event)classifiable;
            if(event.payload.get("customer")!=null) {
                customer = (Customer) event.payload.get("customer");
            }
        }
        if(customer ==null) {
            LOG.warn("Unable to classify Classifiable - no Customer found.");
            return;
        }
        for(Classifier c : classifiable.getClassifiers()) {
            // TODO: classify customer

        }
    }
}
