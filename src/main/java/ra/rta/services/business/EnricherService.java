package ra.rta.services.business;

import java.util.List;
import java.util.Set;
import com.google.common.cache.LoadingCache;
import ra.rta.models.Account;
import ra.rta.models.EnrichableEvent;
import ra.rta.models.Enricher;

/**
 * Enrich Events implementing EnrichedEvent interface.
 * <p/>
 * Created by Brian on 8/4/15
 */
public class EnricherService {

	private LoadingCache<String, Set<Account>> accountCache;

	public EnricherService() {
	}

	public void enrich(EnrichableEvent enrichableEvent) throws Exception {
		List<Enricher> enrichers = enrichableEvent.getEnrichers();
		for (Enricher enricher : enrichers) {
			enricher.enrich(enrichableEvent);
		}
	}

}
