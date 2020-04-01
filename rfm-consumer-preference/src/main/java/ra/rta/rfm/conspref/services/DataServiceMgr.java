package ra.rta.rfm.conspref.services;

import java.util.Map;
import java.util.concurrent.Semaphore;

import com.datastax.driver.core.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.connectors.cassandra.CassandraMgr;

/**
 *
 */
public final class DataServiceMgr {

	private static final Logger log = LoggerFactory.getLogger(DataServiceMgr.class);

	private static Semaphore lock = new Semaphore(1);

	private static DataServiceMgr instance;

	private CustomerDataService customerDataService;
	private ErrorsDataService errorsDataService;
	private GroupDataService groupDataService;
	private RFMSummaryDataService rfmSummaryDataService;
	private TransactionDataService transactionDataService;
	private TransformDataService transformDataService;

	private DataServiceMgr() {}

	public static DataServiceMgr init(Map<String, String> properties) {
		try {
			lock.acquire();
			if (instance!=null) {
				lock.release();
				return instance;
			}
			instance = new DataServiceMgr();
			Session session = CassandraMgr.init(properties).getSession();
			instance.groupDataService = new GroupDataService(session);
			instance.customerDataService = new CustomerDataService(session);
			instance.errorsDataService = new ErrorsDataService(session);
			instance.rfmSummaryDataService = new RFMSummaryDataService(session);
			instance.transactionDataService = new TransactionDataService(session);
			instance.transformDataService = new TransformDataService(session);
		} catch (InterruptedException e) {
			log.warn("error setting up DataServiceManager", e);
		} finally {
			lock.release();
		}
		return instance;
	}

	public static DataServiceMgr getInstance() {
		return instance;
	}

	public CustomerDataService getCustomerDataService() {
		return customerDataService;
	}

	public ErrorsDataService getErrorsDataService() {
		return errorsDataService;
	}

	public GroupDataService getGroupDataService() {
		return groupDataService;
	}

	public RFMSummaryDataService getRfmSummaryDataService() {
		return rfmSummaryDataService;
	}

	public TransactionDataService getTransactionDataService() {
		return transactionDataService;
	}

	public TransformDataService getTransformDataService() {
		return transformDataService;
	}

}
