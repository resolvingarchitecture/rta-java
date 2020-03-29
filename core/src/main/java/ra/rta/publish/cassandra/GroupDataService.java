package ra.rta.publish.cassandra;

import java.util.HashMap;
import java.util.Map;

import ra.rta.models.Cluster;

/**
 * Provides persistence for the Group domain model.
 */
public class GroupDataService {

	private final GroupRepository partnerRepo;

	GroupDataService(GroupRepository partnerRepo) {
		this.partnerRepo = partnerRepo;
	}

	public Map<String, Cluster> getAllActivePartnersMap() throws Exception {
		Map<String, Cluster> partnerMap = new HashMap<>();
		Iterable<Cluster> partners = partnerRepo.findActive();
		for (Cluster cluster : partners) {
			partnerMap.put(cluster.getName(), cluster);
		}
		return partnerMap;
	}

}
