package ra.rta.services.business.tasks;

import java.util.LinkedHashSet;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.LoadingCache;
import ra.rta.services.business.ClassificationService;
import ra.rta.services.data.DataServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.models.*;

public class TransactionClassifier implements Classifier {

	private static Logger LOG = LoggerFactory.getLogger(TransactionClassifier.class);

	@Override
	public void classify(ClassifiableEvent event, LoadingCache<String, LinkedHashSet<KPI>> exactMatchTermcodeCache) throws Exception {
		Transaction transaction = (Transaction) event.getEntity();
		if (transaction.getPayee() == null || "".equals(transaction.getPayee())) {
			transaction.setStatus(Transaction.Status.Rejected);
			// String errorMsg = transaction.getId().toString();
			String errorMsg = "null or empty payee";
			EventException exception = new EventException(ClassificationService.class.getSimpleName(), 105, errorMsg, event);
			DataServiceManager.getErrorsDataService().save(exception, event);
			return;
		}
//		String partnerName = transaction.getPartner().getName();
//		String nowDateStr = DateUtility.timestampToSimpleDateString(new Date());
//		WANDDataService wandDataService = dataServiceManager.getWandDataService();
		try {
			LinkedHashSet<KPI> cachedTermcodes = exactMatchTermcodeCache.get(transaction.getPayee());
			for(KPI KPI : cachedTermcodes) {
				transaction.getKPIS().add((KPI) KPI.clone());
			}
		} catch (ExecutionException e) {
			transaction.setStatus(Transaction.Status.Rejected);
//			transaction.setSuspended(true);
//			ExactMatchFailure exactMatchFailure = wandDataService.loadExactMatchFailure(partnerName, transaction.getPayee());
//			if (exactMatchFailure == null) {
//				exactMatchFailure = new
//						ExactMatchFailure(transaction.getPayee());
//				exactMatchFailure.setCount(1);
//				exactMatchFailure.setFirstSeen(nowDateStr);
//				exactMatchFailure.setLastSeen(nowDateStr);
//				exactMatchFailure.setType(WANDType.determineWANDType(transaction.getType()).name());
//				exactMatchFailure.setVehicle(WANDVehicle.determineWANDVehicle(transaction).name());
//			} else {
//				exactMatchFailure.setCount(exactMatchFailure.getCount() + 1);
//				exactMatchFailure.setLastSeen(nowDateStr);
//			}
//			wandDataService.save(partnerName, exactMatchFailure);
//			DataServiceManager.getTransactionDataService().suspend(transaction);
//			String errorMsg = transaction.getPayee();
//			EventException exception = new EventException(ClassificationService.class.getSimpleName(), 106,
//					errorMsg, event);
//			DataServiceManager.getErrorsDataService().save(exception, event);
			e.printStackTrace();
			return;
		}
//		if(transaction.getSuspended()) {
//			DataServiceManager.getTransactionDataService().unsuspend(transaction);
//		}
	}
}
