package ra.rta.services.data;

import java.util.HashMap;
import java.util.Map;

import ra.rta.models.Group;

/**
 * Provides persistence for the Partner domain model.
 */
public class GroupDataService {

	private final GroupRepository partnerRepo;

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
