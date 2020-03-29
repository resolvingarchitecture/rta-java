package ra.rta.bolts;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.models.Enrichable;
import ra.rta.models.Event;
import ra.rta.models.EventException;
import ra.rta.services.business.EnricherService;
import ra.rta.services.business.events.UCIDNotProvidedException;
import ra.rta.services.data.DataServiceManager;

public class ContentEnricherBolt extends BaseEventEmitterBolt {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ContentEnricherBolt.class);

	private EnricherService enricherService;

	private long maxRetries;

	@Override
	@SuppressWarnings("hiding")
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		super.prepare(map, topologyContext, outputCollector);
//		maxRetries = (Long) map.get("topology.bolts.contentenricher.retries.max");
		enricherService = new EnricherService();
	}

	@Override
	public void execute(Event event) throws Exception {
		if (event instanceof Enrichable) {
			try {
				enricherService.enrich((Enrichable) event);
			} catch (UCIDNotProvidedException e) {
				EventException error = new EventException(ContentEnricherBolt.class.getSimpleName(), 104, event.id+"", event);
				DataServiceManager.getErrorsDataService().save(error, event);
			}
		}
	}

	//	private void retry(Event event) throws Exception {
	//		int retried = event.getEnvelope().getBody().getRecords().get(0).getTried() + 1;
	//		LOG.warn("UCIDNotProvidedException; retries={}", retried);
	//		if (retried >= maxRetries) {
	//			EventException error = new EventException(ContentEnricherBolt.class.getName(), "Event enriching reached threshold of "+maxRetries+".", event);
	//			dataServiceManager.getErrorsDataService().save(error, event.getEntity().getPartner().getName());
	//		} else {
	//			event.getEnvelope().getBody().getRecords().get(0).setTried(retried);
	//			List<ProducerRecord<String, String>> messages = new ArrayList<>();
	//			ByteArrayOutputStream os = new ByteArrayOutputStream();
	//			MAPPER.writeValue(os, event.getEnvelope());
	//			String topic = "transaction";
	//			boolean durable = false;
	//			if (!(event.getEntity() instanceof Transaction)) {
	//				topic = "reference";
	//				durable = true;
	//			}
	//			messageManager.send(topic, new String(os.toByteArray()), durable);
	//		}
	//	}

}
