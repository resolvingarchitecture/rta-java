package ra.rta.bolts.rfm;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import ra.rta.models.KPIIndividualSummary;
import ra.rta.utilities.DateUtility;
import ra.rta.services.data.DataServiceManager;

/**
 * Sums each supplied Customer KPI's frequency and monetary and determines the most recent time across its window.
 */
public class SummarizeIndividualRFMBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(SummarizeIndividualRFMBolt.class);

	private Map map;
	private OutputCollector outputCollector;
	private Map<Integer,Integer> windowLookup;

	private Cluster cluster;
	private Session session;

	@Override
	@SuppressWarnings("hiding")
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.map = map;
		DataServiceManager.setProperties(map);
		this.outputCollector = outputCollector;
		// Load WAND
		windowLookup = new HashMap<>();
		DataServiceManager.getWandDataService().buildWindowSelector(windowLookup);
		// Cassandra preparation
		String cassandraNode = (String)map.get("topology.cassandra.seednode");
		cluster = Cluster.builder().addContactPoint(cassandraNode).build();
		Metadata metadata = cluster.getMetadata();
		LOG.info("{} Connected to cluster: {}", SummarizeIndividualRFMBolt.class.getSimpleName(),
				metadata.getClusterName());
		for (Host host : metadata.getAllHosts()) {
			LOG.info("{} Datacenter: {}; Host: {}; Rack: {}", SummarizeIndividualRFMBolt.class.getSimpleName(),
					host.getDatacenter(), host.getAddress(), host.getRack());
		}
		session = cluster.connect();
	}

	@Override
	public void execute(Tuple tuple) {
		//        LOG.info("Executing "+SummarizeCustomerRFMBolt.class.getSimpleName());
		String partnerName = tuple.getStringByField("name");
		UUID adid = (UUID)tuple.getValueByField("adid");
		Integer termcode = tuple.getIntegerByField("termcode");
		//        LOG.info("Partner.name: "+partnerName+"; adid: "+adid+"; termcode: "+termcode);
		KPIIndividualSummary KPIIndividualSummary = new KPIIndividualSummary();
		KPIIndividualSummary.setAdId(adid);
		KPIIndividualSummary.setTermcode(termcode);
		// Update customer_KPI_rfm_windowed
		try {
			// 1
			int endDate = DateUtility.dateToInt(Calendar.getInstance().getTime());
			KPIIndividualSummary.setDate(endDate);
			int windowDays = windowLookup.get(termcode);
			//            LOG.info("windowDays="+windowDays);
			Calendar beginCalendar = Calendar.getInstance();
			beginCalendar.add(Calendar.DATE, -windowDays);
			int beginDate = DateUtility.dateToInt(beginCalendar.getTime());

			int recency = 0;
			long frequency = 0;
			long monetary = 0;

			// Sum frequency over window
			ResultSet rs = session.execute("SELECT frequency, date FROM "+partnerName+".customer_KPI_frequency where adid="+adid+" and date > "+beginDate+" and termcode="+termcode+";");
			boolean first = true;
			for (Row row : rs) {
				frequency += row.getLong("frequency");
				if(first) {
					recency = row.getInt("date");
				} first = false;
			}

			KPIIndividualSummary.setFrequency(frequency);
			KPIIndividualSummary.setRecency(recency);

			// Sum monetary over window
			rs = session.execute("SELECT monetary FROM "+partnerName+".customer_KPI_monetary where adid="+adid+" and date > "+beginDate+" and  termcode="+termcode+";");
			for (Row row : rs) {
				monetary += row.getLong(0);
			}
			KPIIndividualSummary.setMonetary((double)monetary/100);

			// Insert new summary
			session.execute("INSERT INTO "+partnerName+".customer_KPI_rfm_windowed (adid, termcode, date, window_days, recency, frequency, monetary) VALUES ("+adid+","+termcode+","+endDate+","+windowDays+","+recency+","+frequency+","+monetary+");");

			outputCollector.emit(new Values(partnerName, termcode, KPIIndividualSummary));

		} catch (Exception e) {
			LOG.error(SummarizeIndividualRFMBolt.class.getSimpleName() + " error: " + e);
			outputCollector.reportError(e);
		}

		outputCollector.ack(tuple);

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("name", "termcode", "summary"));
	}

}
