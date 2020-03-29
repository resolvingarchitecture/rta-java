package ra.rta.rfm.conspref.publish;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.BaseEventEndBolt;
import ra.rta.models.Event;
import ra.rta.models.EventException;
import ra.rta.rfm.conspref.models.FinancialTransaction;
import ra.rta.rfm.conspref.services.data.DataServiceManager;

/**
 * Publish final results to Cassandra.
 *
 * Handles multiple records.
 */
public class PublishBolt extends BaseEventEndBolt {

	private static Logger LOG = LoggerFactory.getLogger(PublishBolt.class);

	private static final long serialVersionUID = 1L;

	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		super.prepare(map, topologyContext, outputCollector);

	}

	@Override
	public void execute(Event event) throws Exception {
		if(event.payload.get("trx")!=null) {
			FinancialTransaction financialTransaction = (FinancialTransaction)event.payload.get("trx");

		} else {
			EventException eventException = new EventException(PublishBolt.class.getSimpleName(), 103, "Nothing to publish.", event);
			DataServiceManager.getErrorsDataService().save(eventException, event);
		}
	}

}
