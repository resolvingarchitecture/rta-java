package ra.rta.bolts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.models.Event;
import ra.rta.models.EventException;
import ra.rta.models.FinancialTransaction;
import ra.rta.services.data.DataServiceManager;
import ra.rta.publish.sinks.Sink;

/**
 * Publish final results to Cassandra and Kafka.
 *
 * Handles multiple records.
 */
public class PublishBolt extends BaseEventEndBolt {

	private static Logger LOG = LoggerFactory.getLogger(PublishBolt.class);

	private static final long serialVersionUID = 1L;
	private List<Sink> sinks = new ArrayList<>();

	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		super.prepare(map, topologyContext, outputCollector);
		String sinksStr = (String)map.get("sinks");
		if(sinksStr!=null) {
			String[] sinks = sinksStr.split(",");
			for(String s : sinks) {
				try {
					Sink sink = (Sink)Class.forName(s).getConstructor().newInstance();
					this.sinks.add(sink);
				} catch (Exception e) {
					LOG.warn(e.getLocalizedMessage());
				}
			}
		}
	}

	@Override
	public void execute(Event event) throws Exception {
		if(event.payload.get("trx")!=null) {
			FinancialTransaction financialTransaction = (FinancialTransaction)event.payload.get("trx");
			for(Sink sink : sinks) {
				sink.publish(event);
			}
		} else {
			EventException eventException = new EventException(PublishBolt.class.getSimpleName(), 103, "Nothing to publish.", event);
			DataServiceManager.getErrorsDataService().save(eventException, event);
		}
	}

}
