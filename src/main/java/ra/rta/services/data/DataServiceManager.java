package ra.rta.services.data;

import java.util.Map;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

/**
 * {Purpose of This Class} TODO: Swap internals with Spring
 * Cassandra Created by Brian on 8/25/15
 */
public final class DataServiceManager {

	private static final Logger log = LoggerFactory.getLogger(DataServiceManager.class);

	private static Cluster cluster;
	private static CustomerDataService customerDataService;
	private static AccountDataService accountDataService;
	private static ErrorsDataService errorsDataService;
	private static PartnerDataService partnerDataService;
	private static RFMSummaryDataService rfmSummaryDataService;
	private static TransactionDataService transactionDataService;
	private static TransformDataService transformDataService;
	private static WANDDataService wandDataService;
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
			RepositoryManager repoManager = new RepositoryManager(cluster);
			PartnerRepository partnerRepo = repoManager.getPartnerRepo();
			partnerDataService = new PartnerDataService(partnerRepo);

			@SuppressWarnings("resource")
			Session session = cluster.connect();
			customerDataService = new CustomerDataService(session);
			accountDataService = new AccountDataService(session);
			errorsDataService = new ErrorsDataService(session);
			rfmSummaryDataService = new RFMSummaryDataService(session);
			transactionDataService = new TransactionDataService(session);
			transformDataService = new TransformDataService(session);
			wandDataService = new WANDDataService(session);
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

	public static AccountDataService getAccountDataService() {
		return accountDataService;
	}

	public static CustomerDataService getCustomerDataService() {
		return customerDataService;
	}

	public static ErrorsDataService getErrorsDataService() {
		return errorsDataService;
	}

	public static PartnerDataService getPartnerDataService() {
		return partnerDataService;
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
