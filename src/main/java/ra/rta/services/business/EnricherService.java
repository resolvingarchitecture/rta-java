package ra.rta.services.business;

import java.util.List;
import ra.rta.models.EnrichableEvent;
import ra.rta.models.Enricher;

/**
 *
 */
public class EnricherService {

	public EnricherService() {
	}

	public void enrich(EnrichableEvent enrichableEvent) throws Exception {
		List<Enricher> enrichers = enrichableEvent.getEnrichers();
		for (Enricher enricher : enrichers) {
			enricher.enrich(enrichableEvent);
		}
	}

}
