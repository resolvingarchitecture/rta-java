package ra.rta.rfm.conspref.enrich;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.BaseEventEmitterBolt;
import ra.rta.Event;
import ra.rta.rfm.conspref.models.Record;

public class EnricherBolt extends BaseEventEmitterBolt {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(EnricherBolt.class);


	private long maxRetries;

	@Override
	@SuppressWarnings("hiding")
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		super.prepare(map, topologyContext, outputCollector);
//		maxRetries = (Long) map.get("topology.bolts.contentenricher.retries.max");
	}

	@Override
	public void execute(Event event) throws Exception {
		List<Record> records = (List<Record>)event.payload.get("records");
		for(Record r : records) {
			if(r.customer ==null) {
				LOG.warn("No Individual found; unable to enrich.");
				return;
			}
			if (r.customer.birthYear > 1900 && (r.customer.age < 0 || r.customer.age > 200)) {
				r.customer.age = Calendar.getInstance().get(Calendar.YEAR) - r.customer.birthYear;
				r.customer.save = true;
			}
			if (r.customer.openDateMonths == null && r.customer.openDate != null) {
				r.customer.openDateMonths =
						Months.monthsBetween(
								new LocalDate(r.customer.openDate).withDayOfMonth(1),
								new LocalDate(new Date()).withDayOfMonth(1)
						).getMonths();
				r.customer.save = true;
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
