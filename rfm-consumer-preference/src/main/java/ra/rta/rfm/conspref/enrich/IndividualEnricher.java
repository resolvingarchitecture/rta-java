package ra.rta.rfm.conspref.enrich;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.models.Enrichable;
import ra.rta.models.Enricher;
import ra.rta.models.Event;
import ra.rta.rfm.conspref.models.*;

/**
 *
 */
public class IndividualEnricher implements Enricher {

	private static final Logger LOG = LoggerFactory.getLogger(IndividualEnricher.class);

	@Override
	public void enrich(Enrichable enrichable) throws Exception {
		Identity identity = null;
		if(enrichable instanceof ra.rta.models.Event) {
			ra.rta.models.Event event = (Event)enrichable;
			if(event.indId > 0) {
				identity = (Identity) event.payload.get("individual");
			}
		}
		if(identity ==null) {
			LOG.warn("No Individual found in Enrichable; unable to enrich.");
			return;
		}
		if (identity.birthYear > 1900 && (identity.age < 0 || identity.age > 200)) {
			identity.age = Calendar.getInstance().get(Calendar.YEAR) - identity.birthYear;
			identity.save = true;
		}
	}
}
