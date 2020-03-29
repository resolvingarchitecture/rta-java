package ra.rta.publish.cassandra;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import ra.rta.models.Cluster;

import java.util.UUID;

public interface GroupRepository extends CassandraRepository<Cluster, UUID> {

	@Query("select * from ra.group where active = true")
	Iterable<Cluster> findActive();

}
