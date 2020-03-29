package ra.rta.services.business.tasks;

import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.classify.Classifiable;
import ra.rta.classify.Classifier;
import ra.rta.models.*;

import java.util.LinkedHashSet;

/**
 * {
 */
public class IndividualKPIClassifier implements Classifier {

    private static final Logger LOG = LoggerFactory.getLogger(ra.rta.classify.IndividualKPIClassifier.class);

    @Override
    public void classify(Classifiable classifiable, LoadingCache<String, LinkedHashSet<RFMKPI>> exactMatchTermcodeCache) {
        Individual individual = null;
        if(classifiable instanceof Event) {
            Event event = (Event)classifiable;
            if(event.payload.get("individual")!=null) {
                individual = (Individual)event.payload.get("individual");
            }
        }
        if(individual==null) {
            LOG.warn("Unable to classify Classifiable - no Individual found.");
            return;
        }
        for(Classifier c : classifiable.getClassifiers()) {
            // TODO: classify individual
        }
    }
}
