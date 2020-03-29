package ra.rta.rfm.conspref.analyze;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import ra.rta.rfm.conspref.models.KPIGroupSummary;
import ra.rta.rfm.conspref.models.KPIIndividualSummary;
import ra.rta.rfm.conspref.utilities.DateUtility;

/**
 * Sums each supplied Group KPI's frequency and monetary and determines the most recent time across its window.
 */
public class SummarizeGroupRFMBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(SummarizeGroupRFMBolt.class);

	private OutputCollector outputCollector;

	private Cluster cluster;
	private Session session;

	@Override
	@SuppressWarnings("hiding")
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.outputCollector = outputCollector;
		// Cassandra preparation
		String cassandraNode = (String)map.get("topology.cassandra.seednode");
		cluster = Cluster.builder().addContactPoint(cassandraNode).build();
		Metadata metadata = cluster.getMetadata();
		LOG.info("{} Connected to cluster: {}", SummarizeGroupRFMBolt.class.getSimpleName(),
				metadata.getClusterName());
		for (Host host : metadata.getAllHosts()) {
			LOG.info("{} Datacenter: {}; Host: {}; Rack: {}", SummarizeGroupRFMBolt.class.getSimpleName(),
					host.getDatacenter(), host.getAddress(), host.getRack());
		}
		session = cluster.connect();
	}

	@Override
	public void execute(Tuple tuple) {
		String groupName = tuple.getStringByField("name");
		int termcode = tuple.getIntegerByField("termcode");
		KPIIndividualSummary KPIIndividualSummary = (KPIIndividualSummary)tuple.getValueByField("summary");
		KPIGroupSummary KPIGroupSummary = new KPIGroupSummary();
		KPIGroupSummary.setTermcode(KPIIndividualSummary.getTermcode());
		KPIGroupSummary.setDate(KPIIndividualSummary.getDate());
		KPIGroupSummary.setWindowDays(KPIIndividualSummary.getWindowDays());
		// Update partner_KPI_rfm_windowed
		try {
			boolean update = false;

			// Get latest summary
			ResultSet rs = session.execute("SELECT * FROM "+groupName+".partner_KPI_rfm_windowed where termcode="+ KPIIndividualSummary.getTermcode()+" limit 1;");
			Row row = rs.one();
			if (row == null) {
				KPIGroupSummary.setRecencyEarliest(KPIIndividualSummary.getRecency());
				KPIGroupSummary.setRecencyLatest(KPIIndividualSummary.getRecency());
				KPIGroupSummary.setFrequencyLeast(KPIIndividualSummary.getFrequency());
				KPIGroupSummary.setFrequencyMost(KPIIndividualSummary.getFrequency());
				KPIGroupSummary.setMonetaryLeast(KPIIndividualSummary.getMonetary());
				KPIGroupSummary.setMonetaryMost(KPIIndividualSummary.getMonetary());
				update = true;
			} else {
				KPIGroupSummary.setRecencyEarliest(row.getInt("recency_earliest"));
				KPIGroupSummary.setRecencyLatest(row.getInt("recency_latest"));
				KPIGroupSummary.setRecencyBucket2Floor(row.getInt("recency_bucket_2_floor"));
				KPIGroupSummary.setRecencyBucket3Floor(row.getInt("recency_bucket_3_floor"));
				KPIGroupSummary.setFrequencyLeast(row.getLong("frequency_least"));
				KPIGroupSummary.setFrequencyMost(row.getLong("frequency_most"));
				KPIGroupSummary.setFrequencyBucket2Floor(row.getLong("frequency_bucket_2_floor"));
				KPIGroupSummary.setFrequencyBucket3Floor(row.getLong("frequency_bucket_3_floor"));
				KPIGroupSummary.setMonetaryLeast(row.getDouble("monetary_least"));
				KPIGroupSummary.setMonetaryMost(row.getDouble("monetary_most"));
				KPIGroupSummary.setMonetaryBucket2Floor(row.getDouble("monetary_bucket_2_floor"));
				KPIGroupSummary.setMonetaryBucket3Floor(row.getDouble("monetary_bucket_3_floor"));

				if (KPIIndividualSummary.getRecency() < KPIGroupSummary.getRecencyEarliest()) {
					KPIGroupSummary.setRecencyEarliest(KPIIndividualSummary.getRecency());
					update = calculateRecencyBuckets(KPIGroupSummary);
				} else if (KPIIndividualSummary.getRecency() > KPIGroupSummary.getRecencyLatest()) {
					KPIGroupSummary.setRecencyLatest(KPIIndividualSummary.getRecency());
					update = calculateRecencyBuckets(KPIGroupSummary);
				}
				if (KPIIndividualSummary.getFrequency() < KPIGroupSummary.getFrequencyLeast()) {
					KPIGroupSummary.setFrequencyLeast(KPIIndividualSummary.getFrequency());
					calculateFrequencyBuckets(KPIGroupSummary); update = true;
				} else if (KPIIndividualSummary.getFrequency() > KPIGroupSummary.getFrequencyMost()) {
					KPIGroupSummary.setFrequencyMost(KPIIndividualSummary.getFrequency());
					calculateFrequencyBuckets(KPIGroupSummary); update = true;
				}
				if (KPIIndividualSummary.getMonetary() < KPIGroupSummary.getMonetaryLeast()) {
					KPIGroupSummary.setMonetaryLeast(KPIIndividualSummary.getMonetary());
					calculateMonetaryBuckets(KPIGroupSummary); update = true;
				} else if (KPIIndividualSummary.getMonetary() > KPIGroupSummary.getMonetaryMost()) {
					KPIGroupSummary.setMonetaryMost(KPIIndividualSummary.getMonetary());
					calculateMonetaryBuckets(KPIGroupSummary); update = true;
				}
			}

			if (update) {
				session.execute("UPDATE " + groupName + ".partner_KPI_rfm_windowed SET window_days = " + KPIGroupSummary.getWindowDays() + ", recency_earliest = " + KPIGroupSummary.getRecencyEarliest() + ", recency_latest = " + KPIGroupSummary.getRecencyLatest() + ", recency_bucket_2_floor = " + KPIGroupSummary.getRecencyBucket2Floor() + ", recency_bucket_3_floor = " + KPIGroupSummary.getRecencyBucket3Floor() + ", frequency_least = " + KPIGroupSummary.getFrequencyLeast() + ", frequency_most = " + KPIGroupSummary.getFrequencyMost() + ", frequency_bucket_2_floor =" + KPIGroupSummary.getFrequencyBucket2Floor() + ", frequency_bucket_3_floor = " + KPIGroupSummary.getFrequencyBucket3Floor() + ", monetary_least = " + KPIGroupSummary.getMonetaryLeast() + ", monetary_most = " + KPIGroupSummary.getMonetaryMost() + ", monetary_bucket_2_floor = " + KPIGroupSummary.getMonetaryBucket2Floor() + ", monetary_bucket_3_floor = " + KPIGroupSummary.getMonetaryBucket3Floor() +" WHERE termcode = " + KPIGroupSummary.getTermcode() + " AND date = " + KPIGroupSummary.getDate() + ";");
			}

		} catch (Exception e) {
			LOG.error(SummarizeGroupRFMBolt.class.getSimpleName() + " error: " + e);
			outputCollector.reportError(e);
		}

		outputCollector.ack(tuple);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		//
	}

	private static boolean calculateRecencyBuckets(KPIGroupSummary KPIGroupSummary) throws Exception {
		boolean updated = false;
		if (KPIGroupSummary.getRecencyEarliest() > 0) { // Must be a
			Calendar recencyLatestCal = Calendar.getInstance();
			recencyLatestCal.setTime(DateUtility.intToDate(KPIGroupSummary.getRecencyLatest()));
			long recencyWindowLatest = recencyLatestCal.getTimeInMillis();

			Calendar recencyEarliestCal = Calendar.getInstance();
			recencyEarliestCal.setTime(DateUtility.intToDate(KPIGroupSummary.getRecencyEarliest()));
			long recencyWindowEarliest = recencyEarliestCal.getTimeInMillis();

			int numberDaysDiffRecency = (int) TimeUnit.MILLISECONDS.toDays(Math.abs(recencyWindowLatest - recencyWindowEarliest));
			int numberDaysPerBucket = (int) Math.floor(numberDaysDiffRecency / 3);
			recencyEarliestCal.add(Calendar.DATE, numberDaysPerBucket);
			KPIGroupSummary.setRecencyBucket2Floor(DateUtility.dateToInt(recencyEarliestCal.getTime()));

			recencyEarliestCal.add(Calendar.DATE, numberDaysPerBucket);
			KPIGroupSummary.setRecencyBucket3Floor(DateUtility.dateToInt(recencyEarliestCal.getTime()));
			updated = true;
		}
		return updated;
	}

	private static void calculateFrequencyBuckets(KPIGroupSummary KPIGroupSummary) throws Exception {
		long frequencyRange = KPIGroupSummary.getFrequencyMost() - KPIGroupSummary.getFrequencyLeast();
		long frequencyPerBucket = (long)Math.floor(frequencyRange / 3);
		KPIGroupSummary.setFrequencyBucket2Floor(KPIGroupSummary.getFrequencyLeast() + frequencyPerBucket);
		KPIGroupSummary.setFrequencyBucket3Floor(KPIGroupSummary.getFrequencyBucket2Floor() + frequencyPerBucket);
	}

	private static void calculateMonetaryBuckets(KPIGroupSummary KPIGroupSummary) throws Exception {
		double monetaryRange = KPIGroupSummary.getMonetaryMost() - KPIGroupSummary.getMonetaryLeast();
		double monetaryPerBucket = Math.floor(monetaryRange / 3);
		KPIGroupSummary.setMonetaryBucket2Floor(KPIGroupSummary.getMonetaryLeast() + monetaryPerBucket);
		KPIGroupSummary.setMonetaryBucket3Floor(KPIGroupSummary.getMonetaryBucket2Floor() + monetaryPerBucket);
	}
}
