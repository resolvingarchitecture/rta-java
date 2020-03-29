package ra.rta.services.data;

import java.util.Map;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import ra.rta.classify.WANDDataService;
import ra.rta.publish.cassandra.ErrorsDataService;
import ra.rta.publish.cassandra.GroupDataService;
import ra.rta.publish.cassandra.GroupRepository;
import ra.rta.publish.cassandra.IndividualDataService;
import ra.rta.publish.cassandra.RFMSummaryDataService;
import ra.rta.publish.cassandra.CassandraRepoMgr;
import ra.rta.publish.cassandra.TransactionDataService;
import ra.rta.transform.TransformDataService;

/**
 *
 */
public final class DataServiceManager {

	private static final Logger log = LoggerFactory.getLogger(ra.rta.publish.cassandra.DataServiceManager.class);

	private static Cluster cluster;
	private static ra.rta.publish.cassandra.IndividualDataService individualDataService;
	private static ra.rta.publish.cassandra.ErrorsDataService errorsDataService;
	private static ra.rta.publish.cassandra.GroupDataService groupDataService;
	private static ra.rta.publish.cassandra.RFMSummaryDataService rfmSummaryDataService;
	private static ra.rta.publish.cassandra.TransactionDataService transactionDataService;
	private static ra.rta.transform.TransformDataService transformDataService;
	private static ra.rta.classify.WANDDataService wandDataService;
	private static Semaphore lock = new Semaphore(1);
	private static volatile boolean setup = false;

	private DataServiceManager() {
		//
	}

	public static void setProperties(Map<String, String> properties) {
		try {
			lock.acquire();
			if (setup) {
				return;
			}
			String seedNode = properties.get("topology.cassandra.seednode");
			cluster = Cluster.builder().addContactPoint(seedNode).build();
//			cluster.getConfiguration().getPoolingOptions().setCoreConnectionsPerHost(HostDistance.LOCAL, 12);
//			cluster.getConfiguration().getPoolingOptions().setCoreConnectionsPerHost(HostDistance.REMOTE, 12);
//			cluster.getConfiguration().getPoolingOptions().setMaxConnectionsPerHost(HostDistance.LOCAL, 16);
//			cluster.getConfiguration().getPoolingOptions().setMaxConnectionsPerHost(HostDistance.REMOTE, 16);
			CassandraRepoMgr repoManager = new CassandraRepoMgr(cluster);
			GroupRepository partnerRepo = repoManager.getPartnerRepo();
			groupDataService = new ra.rta.publish.cassandra.GroupDataService(partnerRepo);

			@SuppressWarnings("resource")
			Session session = cluster.connect();
			individualDataService = new ra.rta.publish.cassandra.IndividualDataService(session);
			errorsDataService = new ra.rta.publish.cassandra.ErrorsDataService(session);
			rfmSummaryDataService = new ra.rta.publish.cassandra.RFMSummaryDataService(session);
			transactionDataService = new ra.rta.publish.cassandra.TransactionDataService(session);
			transformDataService = new ra.rta.transform.TransformDataService(session);
			wandDataService = new ra.rta.classify.WANDDataService(session);
			setup = true;
		} catch (InterruptedException e) {
			log.warn("error setting up DataServiceManager", e);
		} finally {
			lock.release();
		}
	}

	public static void close() {
		if (cluster != null) {
			cluster.close();
		}
	}

	public static IndividualDataService getIndividualDataService() {
		return individualDataService;
	}

	public static ErrorsDataService getErrorsDataService() {
		return errorsDataService;
	}

	public static GroupDataService getGroupDataService() {
		return groupDataService;
	}

	public static RFMSummaryDataService getRfmSummaryDataService() {
		return rfmSummaryDataService;
	}

	public static TransactionDataService getTransactionDataService() {
		return transactionDataService;
	}

	public static TransformDataService getTransformDataService() {
		return transformDataService;
	}

	public static WANDDataService getWandDataService() {
		return wandDataService;
	}
}
