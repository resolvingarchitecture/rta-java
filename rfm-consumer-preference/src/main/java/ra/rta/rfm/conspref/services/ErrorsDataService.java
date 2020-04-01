package ra.rta.rfm.conspref.services;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import ra.rta.EventException;
import ra.rta.utilities.JSONUtil;

public class ErrorsDataService extends BaseDataService {

	ErrorsDataService(Session session) {
		super(session);
	}

	public void save(EventException exception) throws Exception {
		SimpleStatement stmt = new SimpleStatement(
				"INSERT INTO errors (id, event_id, date, component, code, message, data) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		PreparedStatement pstmt = session.prepare(stmt);
		BoundStatement bstmt = pstmt.bind(exception.id, exception.event.id, exception.timestamp,
				exception.component, exception.code, exception.message, JSONUtil.MAPPER.writeValueAsString(exception.event));
		session.execute(bstmt);
	}
}
