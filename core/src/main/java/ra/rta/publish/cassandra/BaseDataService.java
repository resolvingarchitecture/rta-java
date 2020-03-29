package ra.rta.publish.cassandra;

import com.datastax.driver.core.Session;

/**
 *
 */
public abstract class BaseDataService {

    protected Session session;

    public BaseDataService(Session session) {
        this.session = session;
    }

}
