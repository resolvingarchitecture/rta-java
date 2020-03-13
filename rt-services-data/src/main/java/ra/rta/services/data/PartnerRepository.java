package ra.rta.services.data;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import ra.rta.models.Partner;

import java.util.UUID;

public interface PartnerRepository extends CassandraRepository<Partner, UUID> {

	@Query("select * from ra.partner where active = true")
	Iterable<Partner> findActive();

}
