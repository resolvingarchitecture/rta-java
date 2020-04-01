package ra.rta.rfm.conspref.analyze;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import ra.rta.connectors.cassandra.CassandraMgr;
import ra.rta.rfm.conspref.classify.WANDMgr;
import ra.rta.rfm.conspref.models.KPICustomerSummary;
import ra.rta.rfm.conspref.utilities.DateUtility;
import ra.rta.rfm.conspref.services.DataServiceMgr;

/**
 * Sums each supplied Customer KPI's frequency and monetary and determines the most recent time across its window.
 */
public class SummarizeCustomerRFMBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(SummarizeCustomerRFMBolt.class);

	private Map map;
	private OutputCollector outputCollector;
	private Map<Integer,Integer> windowLookup;

	private Session session;

	@Override
	@SuppressWarnings("hiding")
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.map = map;
		this.outputCollector = outputCollector;
		// Load WAND
		windowLookup = new HashMap<>();
		session = CassandraMgr.init(map).getSession();
		WANDMgr.init(map).buildWindowSelector(windowLookup);
	}

	@Override
	public void execute(Tuple tuple) {
		LOG.info("Executing "+SummarizeCustomerRFMBolt.class.getSimpleName());
		KPICustomerSummary sum = new KPICustomerSummary();
		sum.gId = tuple.getIntegerByField("gId");
		sum.cId = tuple.getIntegerByField("cId");;
		sum.tCode = tuple.getIntegerByField("tCode");
		// Update customer_KPI_rfm_windowed
		try {
			// 1
			sum.date = Calendar.getInstance().getTime().getTime();
			int windowDays = windowLookup.get(sum.tCode);
			LOG.info("windowDays="+windowDays);
			Calendar beginCalendar = Calendar.getInstance();
			beginCalendar.add(Calendar.DATE, -windowDays);
			int beginDate = DateUtility.dateToInt(beginCalendar.getTime());

			int recency = 0;
			long frequency = 0;
			long monetary = 0;

			// Sum frequency over window
			ResultSet rs = session.execute("SELECT frequency, date FROM customer_KPI_frequency where gId="+sum.gId+" and cId="+sum.cId+" and date > "+beginDate+" and tCode="+sum.tCode+";");
			boolean first = true;
			for (Row row : rs) {
				frequency += row.getLong("frequency");
				if(first) {
					recency = row.getInt("date");
				} first = false;
			}

			sum.frequency = frequency;
			sum.recency = recency;

			// Sum monetary over window
			rs = session.execute("SELECT monetary FROM customer_KPI_monetary where gId="+sum.gId+" and date > "+beginDate+" and  termcode="+sum.tCode+";");
			for (Row row : rs) {
				monetary += row.getLong(0);
			}
			sum.monetary = (double)monetary/100;

			// Insert new summary
			session.execute("INSERT INTO customer_KPI_rfm_windowed (gId, termcode, date, window_days, recency, frequency, monetary) VALUES ("+sum.gId+","+sum.tCode+","+sum.date+","+windowDays+","+recency+","+frequency+","+monetary+");");

			outputCollector.emit(new Values(sum.gId, sum.tCode, sum));

		} catch (Exception e) {
			LOG.error(SummarizeCustomerRFMBolt.class.getSimpleName() + " error: " + e);
			outputCollector.reportError(e);
		}

		outputCollector.ack(tuple);

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("name", "termcode", "summary"));
	}

}
