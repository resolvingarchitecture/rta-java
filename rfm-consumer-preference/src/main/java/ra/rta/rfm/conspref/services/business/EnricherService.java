package ra.rta.rfm.conspref.services.business;

import java.util.List;
import ra.rta.rfm.conspref.models.Enrichable;
import ra.rta.rfm.conspref.models.Enricher;

/**
 *
 */
public class EnricherService {

	public EnricherService() {
	}

	public void enrich(Enrichable enrichable) throws Exception {
		List<Enricher> enrichers = enrichable.getEnrichers();
		for (Enricher enricher : enrichers) {
			enricher.enrich(enrichable);
		}
	}

}
