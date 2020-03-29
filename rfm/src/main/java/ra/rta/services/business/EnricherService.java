package ra.rta.services.business;

import java.util.List;
import ra.rta.enrich.Enrichable;
import ra.rta.enrich.Enricher;

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
