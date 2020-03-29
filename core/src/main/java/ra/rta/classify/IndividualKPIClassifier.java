package ra.rta.classify;

import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.models.*;

import java.util.LinkedHashSet;

/**
 * {
 */
public class IndividualKPIClassifier implements Classifier {

    private static final Logger LOG = LoggerFactory.getLogger(IndividualKPIClassifier.class);

    @Override
    public void classify(Classifiable classifiable, LoadingCache<String, LinkedHashSet<KPI>> exactMatchTermcodeCache) {
        Identity identity = null;
        if(classifiable instanceof Event) {
            Event event = (Event)classifiable;
            if(event.payload.get("individual")!=null) {
                identity = (Identity)event.payload.get("individual");
            }
        }
        if(identity ==null) {
            LOG.warn("Unable to classify Classifiable - no Individual found.");
            return;
        }
        for(Classifier c : classifiable.getClassifiers()) {
            // TODO: classify individual
        }
    }
}
