package ra.rta.rfm.conspref.services;

import java.util.ArrayList;
import java.util.List;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/**
 * Provides persistence for the Partner domain model.
 */
public class GroupDataService extends BaseDataService {


	GroupDataService(Session session) {
		super(session);
	}

	public List<Integer> getAllActiveGroups() throws Exception {
		List<Integer> gIds = new ArrayList<>();
		String cql = "SELECT id FROM group where active=true;";
		ResultSet rows = session.execute(cql);
		for(Row row : rows){
			gIds.add(row.getInt(0));
		}
		return gIds;
	}

}
