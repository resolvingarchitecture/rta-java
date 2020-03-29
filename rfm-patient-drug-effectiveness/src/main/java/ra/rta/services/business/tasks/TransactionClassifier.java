package ra.rta.services.business.tasks;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.LoadingCache;
import ra.rta.utilities.DateUtility;
import ra.rta.services.business.ClassificationService;
import ra.rta.services.business.events.TransactionEvent;
import ra.rta.services.data.DataServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.models.*;
import ra.rta.services.data.WANDDataService;

public class TransactionClassifier implements Classifier {

	private static Logger LOG = LoggerFactory.getLogger(TransactionClassifier.class);

	@Override
	public void classify(Classifiable classifiable, LoadingCache<String, LinkedHashSet<KPI>> exactMatchTermcodeCache) throws Exception {
		if(classifiable instanceof TransactionEvent) {
			TransactionEvent eventIn = (TransactionEvent)classifiable;
			if (eventIn.financialTransaction.payee == null || "".equals(eventIn.financialTransaction.payee)) {
				eventIn.financialTransaction.status = FinancialTransaction.Status.Rejected;
				// String errorMsg = transaction.getId().toString();
				String errorMsg = "null or empty payee";
				EventException exception = new EventException(ClassificationService.class.getSimpleName(), 105, errorMsg, eventIn);
				DataServiceManager.getErrorsDataService().save(exception, eventIn);
				return;
			}
			String nowDateStr = DateUtility.timestampToSimpleDateString(new Date());
			WANDDataService wandDataService = DataServiceManager.getWandDataService();
			try {
				LinkedHashSet<KPI> cachedTermcodes = exactMatchTermcodeCache.get(eventIn.financialTransaction.payee);
				for (KPI kpi : cachedTermcodes) {
					eventIn.financialTransaction.kpis.add((KPI) kpi.clone());
				}
			} catch (ExecutionException e) {
				eventIn.financialTransaction.status = FinancialTransaction.Status.Rejected;
				eventIn.financialTransaction.suspended = true;
				ExactMatchFailure exactMatchFailure = wandDataService.loadExactMatchFailure(eventIn.groupId, eventIn.financialTransaction.payee);
				if (exactMatchFailure == null) {
					exactMatchFailure = new
							ExactMatchFailure(transaction.getPayee());
					exactMatchFailure.setCount(1);
					exactMatchFailure.setFirstSeen(nowDateStr);
					exactMatchFailure.setLastSeen(nowDateStr);
					exactMatchFailure.setType(WANDType.determineWANDType(transaction.getType()).name());
					exactMatchFailure.setVehicle(WANDVehicle.determineWANDVehicle(transaction).name());
				} else {
					exactMatchFailure.setCount(exactMatchFailure.getCount() + 1);
					exactMatchFailure.setLastSeen(nowDateStr);
				}
				wandDataService.save(partnerName, exactMatchFailure);
				DataServiceManager.getTransactionDataService().suspend(transaction);
				String errorMsg = transaction.getPayee();
				EventException exception = new EventException(ClassificationService.class.getSimpleName(), 106,
						errorMsg, event);
				DataServiceManager.getErrorsDataService().save(exception, event);
					e.printStackTrace();
					return;
			}
			if(eventIn.financialTransaction.suspended) {
				DataServiceManager.getTransactionDataService().unsuspend(eventIn.financialTransaction);
			}
		}
	}
}
