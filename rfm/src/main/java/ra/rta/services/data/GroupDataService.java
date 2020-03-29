package ra.rta.services.data;

import java.util.HashMap;
import java.util.Map;

import ra.rta.models.Group;
import ra.rta.publish.cassandra.GroupRepository;

/**
 * Provides persistence for the Partner domain model.
 */
public class GroupDataService {

	private final ra.rta.publish.cassandra.GroupRepository partnerRepo;

	GroupDataService(GroupRepository partnerRepo) {
		this.partnerRepo = partnerRepo;
	}

	public Map<String, Group> getAllActivePartnersMap() throws Exception {
		Map<String, Group> partnerMap = new HashMap<>();
		Iterable<Group> partners = partnerRepo.findActive();
		for (Group group : partners) {
			partnerMap.put(group.getName(), group);
		}
		return partnerMap;
	}

}
