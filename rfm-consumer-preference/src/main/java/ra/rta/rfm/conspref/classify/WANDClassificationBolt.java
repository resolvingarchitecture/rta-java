package ra.rta.rfm.conspref.classify;

import java.util.*;
import java.util.concurrent.ExecutionException;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.BaseEventEmitterBolt;
import ra.rta.Event;
import ra.rta.EventException;
import ra.rta.classify.KPI;
import ra.rta.rfm.conspref.models.*;
import ra.rta.rfm.conspref.services.DataServiceMgr;

/**
 * Add associated KPIs using WAND Taxonomies (https://www.wandinc.com/wand-taxonomies)
 */
public class WANDClassificationBolt extends BaseEventEmitterBolt {

	private static final long serialVersionUID = 1L;

	private Logger LOG = LoggerFactory.getLogger(WANDClassificationBolt.class);
	private Map args;
	private WANDMgr wandMgr;

	@SuppressWarnings("hiding")
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		super.prepare(map, topologyContext, outputCollector);
		args = map;
		wandMgr = WANDMgr.init(map);
	}

	public void execute(Event event) throws Exception {
		List<Record> records = (List<Record>)event.payload.get("records");
		for(Record r : records) {
			if(r.customer!=null) {
				if (r.trx.payee == null || "".equals(r.trx.payee)) {
					r.trx.status = FinancialTransaction.Status.Rejected;
					EventException exception = new EventException("Classification", 105, "null or empty payee", event);
					DataServiceMgr.getInstance().getErrorsDataService().save(exception);
					return;
				}
				long now = new Date().getTime();
				try {
					LinkedHashSet<? extends KPI> kpis = wandMgr.lookupTermcode(r.trx.payee);
					for (KPI kpi : kpis) {
						r.customer.kpis.add(kpi.clone());
					}
				} catch (Exception e) {
					r.trx.status = FinancialTransaction.Status.Rejected;
					r.trx.suspended = true;
					ExactMatchFailure exactMatchFailure = wandMgr.loadExactMatchFailure((int)event.payload.get("gId"), r.trx.payee);
					if (exactMatchFailure == null) {
						exactMatchFailure = new ExactMatchFailure();
						exactMatchFailure.count = 1;
						exactMatchFailure.firstSeen = now;
						exactMatchFailure.lastSeen = now;
						exactMatchFailure.type = WANDType.determineWANDType(r.trx.type).name();
						exactMatchFailure.vehicle = WANDVehicle.determineWANDVehicle(r.trx).name();
					} else {
						exactMatchFailure.count++;
						exactMatchFailure.lastSeen = now;
					}
					wandMgr.save(r.gId, exactMatchFailure);
					DataServiceMgr.getInstance().getTransactionDataService().suspend(r.trx);
					String errorMsg = r.trx.payee;
					EventException exception = new EventException("Classification", 106, errorMsg, event);
					DataServiceMgr.getInstance().getErrorsDataService().save(exception);
					LOG.warn(e.getLocalizedMessage());
					return;
				}
				if(r.trx.suspended) {
					DataServiceMgr.getInstance().getTransactionDataService().unsuspend(r.trx);
				}
			}
		}
	}


}
