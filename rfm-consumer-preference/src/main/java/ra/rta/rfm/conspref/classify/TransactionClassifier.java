package ra.rta.rfm.conspref.classify;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.LoadingCache;
import ra.rta.models.Classifiable;
import ra.rta.models.Classifier;
import ra.rta.models.EventException;
import ra.rta.models.KPI;
import ra.rta.rfm.conspref.utilities.DateUtility;
import ra.rta.rfm.conspref.services.business.events.TransactionEvent;
import ra.rta.persistence.PersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.rfm.conspref.models.*;

public class TransactionClassifier implements Classifier {

	private static Logger LOG = LoggerFactory.getLogger(TransactionClassifier.class);

	@Override
	public void classify(Classifiable classifiable, LoadingCache<String, LinkedHashSet<ra.rta.models.KPI>> exactMatchTermcodeCache) throws Exception {
		if(classifiable instanceof TransactionEvent) {
			TransactionEvent eventIn = (TransactionEvent)classifiable;
			if (eventIn.financialTransaction.payee == null || "".equals(eventIn.financialTransaction.payee)) {
				eventIn.financialTransaction.status = FinancialTransaction.Status.Rejected;
				// String errorMsg = transaction.getId().toString();
				String errorMsg = "null or empty payee";
				ra.rta.models.EventException exception = new ra.rta.models.EventException(ClassificationService.class.getSimpleName(), 105, errorMsg, eventIn);
				PersistenceManager.getErrorsDataService().save(exception, eventIn);
				return;
			}
			String nowDateStr = DateUtility.timestampToSimpleDateString(new Date());
			WANDDataService wandDataService = PersistenceManager.getWandDataService();
			try {
				LinkedHashSet<ra.rta.models.KPI> cachedTermcodes = exactMatchTermcodeCache.get(eventIn.financialTransaction.payee);
				for (ra.rta.models.KPI kpi : cachedTermcodes) {
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
				PersistenceManager.getTransactionDataService().suspend(transaction);
				String errorMsg = transaction.getPayee();
				ra.rta.models.EventException exception = new EventException(ClassificationService.class.getSimpleName(), 106,
						errorMsg, event);
				PersistenceManager.getErrorsDataService().save(exception, event);
					e.printStackTrace();
					return;
			}
			if(eventIn.financialTransaction.suspended) {
				PersistenceManager.getTransactionDataService().unsuspend(eventIn.financialTransaction);
			}
		}
	}
}
