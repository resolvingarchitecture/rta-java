package ra.rta.publish.cassandra;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.support.CassandraRepositoryFactory;
import org.springframework.data.repository.CrudRepository;
import ra.rta.BaseEventEmitterBolt;
import ra.rta.classify.WANDDataService;
import ra.rta.models.Event;
import ra.rta.transform.TransformDataService;

/**
 * Publish final results to Cassandra and Kafka.
 *
 * Handles multiple records.
 */
public class CassandraBolt extends BaseEventEmitterBolt {

	private static Logger LOG = LoggerFactory.getLogger(CassandraBolt.class);

	private static final long serialVersionUID = 1L;

	private static Cluster cluster;
	private static IndividualDataService individualDataService;
	private static ErrorsDataService errorsDataService;
	private static GroupDataService groupDataService;
	private static RFMSummaryDataService rfmSummaryDataService;
	private static TransactionDataService transactionDataService;
	private static TransformDataService transformDataService;
	private static WANDDataService wandDataService;
	private static Semaphore lock = new Semaphore(1);
	private static volatile boolean setup = false;

	private Session session;
	private Map<String, CrudRepository> repos = new HashMap<>();

	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		super.prepare(map, topologyContext, outputCollector);
		try {
			lock.acquire();
			if (setup) {
				return;
			}
			String keyspace = (String)map.get("topology.keyspace");
			String seedNode = (String)map.get("topology.cassandra.seednode");
			cluster = Cluster.builder().addContactPoint(seedNode).build();
//			cluster.getConfiguration().getPoolingOptions().setCoreConnectionsPerHost(HostDistance.LOCAL, 12);
//			cluster.getConfiguration().getPoolingOptions().setCoreConnectionsPerHost(HostDistance.REMOTE, 12);
//			cluster.getConfiguration().getPoolingOptions().setMaxConnectionsPerHost(HostDistance.LOCAL, 16);
//			cluster.getConfiguration().getPoolingOptions().setMaxConnectionsPerHost(HostDistance.REMOTE, 16);
			session = cluster.connect(keyspace);
			CassandraTemplate template = new CassandraTemplate(session);
			CassandraRepositoryFactory repoFactory = new CassandraRepositoryFactory(template);
			String repoClassesStr = (String)map.get("topology.repositories");
			String[] repoClasses = repoClassesStr.split(",");

			partnerRepo = repoFactory.getRepository(GroupRepository.class);

			setup = true;
		} catch (InterruptedException e) {
			LOG.warn("error setting up DataServiceManager", e);
		} finally {
			lock.release();
		}
	}

	@Override
	public void execute(Event event) throws Exception {

	}

}
