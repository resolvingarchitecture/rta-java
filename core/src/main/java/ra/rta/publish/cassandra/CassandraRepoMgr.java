package ra.rta.publish.cassandra;

import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.support.CassandraRepositoryFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class CassandraRepoMgr {

	public String keyspace = "ra";

	public CassandraRepoMgr(Cluster cluster) {
		@SuppressWarnings("resource")
		Session session = cluster.connect(keyspace);
		CassandraTemplate template = new CassandraTemplate(session);
		CassandraRepositoryFactory repoFactory = new CassandraRepositoryFactory(template);
		partnerRepo = repoFactory.getRepository(GroupRepository.class);
	}

	public GroupRepository getRepo() {
		return partnerRepo;
	}

}
