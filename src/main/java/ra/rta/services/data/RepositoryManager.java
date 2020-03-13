package ra.rta.services.data;

import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.support.CassandraRepositoryFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class RepositoryManager {

	private final PartnerRepository partnerRepo;

	public RepositoryManager(Cluster cluster) {
		@SuppressWarnings("resource")
		Session session = cluster.connect("ra");
		CassandraTemplate template = new CassandraTemplate(session);
		CassandraRepositoryFactory repoFactory = new CassandraRepositoryFactory(template);
		partnerRepo = repoFactory.getRepository(PartnerRepository.class);
	}

	public PartnerRepository getPartnerRepo() {
		return partnerRepo;
	}

}
