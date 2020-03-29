package ra.rta.services.data;

import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.support.CassandraRepositoryFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import ra.rta.publish.cassandra.GroupRepository;

public class RepositoryManager {

	private final ra.rta.publish.cassandra.GroupRepository partnerRepo;

	public RepositoryManager(Cluster cluster) {
		@SuppressWarnings("resource")
		Session session = cluster.connect("ra");
		CassandraTemplate template = new CassandraTemplate(session);
		CassandraRepositoryFactory repoFactory = new CassandraRepositoryFactory(template);
		partnerRepo = repoFactory.getRepository(ra.rta.publish.cassandra.GroupRepository.class);
	}

	public GroupRepository getPartnerRepo() {
		return partnerRepo;
	}

}
