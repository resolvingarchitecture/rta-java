package ra.rta.bolts;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.models.Customer;
import ra.rta.models.Entity;
import ra.rta.models.Event;
import ra.rta.models.EventException;
import ra.rta.models.Transaction;
import ra.rta.services.data.DataServiceManager;

/**
 * Publish final results to Cassandra and Kafka.
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
		Entity entity = event.getEntity();
		if(entity instanceof Transaction) {
			Transaction transaction = (Transaction)entity;
			// publish transaction...
		} else {
			EventException eventException = new EventException(PublishBolt.class.getSimpleName(), 103, entity.getClass().getName(), event);
			DataServiceManager.getErrorsDataService().save(eventException, event);
		}
	}

}
