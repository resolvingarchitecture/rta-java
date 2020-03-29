package ra.rta.services.data;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import ra.rta.models.Group;

import java.util.UUID;

public interface GroupRepository extends CassandraRepository<Group, UUID> {

	@Query("select * from ra.group where active = true")
	Iterable<Group> findActive();

}
