package ra.rta.services.data;

import java.util.HashMap;
import java.util.Map;

import ra.rta.models.Partner;

/**
 * Provides persistence for the Partner domain model.
 */
public class PartnerDataService {

	private final PartnerRepository partnerRepo;

	PartnerDataService(PartnerRepository partnerRepo) {
		this.partnerRepo = partnerRepo;
	}

	public Map<String,Partner> getAllActivePartnersMap() throws Exception {
		Map<String,Partner> partnerMap = new HashMap<>();
		Iterable<Partner> partners = partnerRepo.findActive();
		for (Partner partner : partners) {
			partnerMap.put(partner.getName(), partner);
		}
		return partnerMap;
	}

}
