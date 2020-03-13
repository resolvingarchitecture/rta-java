package ra.rta.topologies.bolts.rfm;

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
import ra.rta.models.KPICustomerSummary;
import ra.rta.models.KPIPartnerSummary;
import ra.rta.models.utilities.DateUtility;

/**
 * Sums each supplied Partner KPI's frequency and monetary and determines the most recent time across its window.
 */
public class SummarizePartnerRFMBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(SummarizePartnerRFMBolt.class);

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
		LOG.info("{} Connected to cluster: {}", SummarizePartnerRFMBolt.class.getSimpleName(),
				metadata.getClusterName());
		for (Host host : metadata.getAllHosts()) {
			LOG.info("{} Datacenter: {}; Host: {}; Rack: {}", SummarizePartnerRFMBolt.class.getSimpleName(),
					host.getDatacenter(), host.getAddress(), host.getRack());
		}
		session = cluster.connect();
	}

	@Override
	public void execute(Tuple tuple) {
		String partnerName = tuple.getStringByField("name");
		int termcode = tuple.getIntegerByField("termcode");
		KPICustomerSummary KPICustomerSummary = (KPICustomerSummary)tuple.getValueByField("summary");
		KPIPartnerSummary KPIPartnerSummary = new KPIPartnerSummary();
		KPIPartnerSummary.setTermcode(KPICustomerSummary.getTermcode());
		KPIPartnerSummary.setDate(KPICustomerSummary.getDate());
		KPIPartnerSummary.setWindowDays(KPICustomerSummary.getWindowDays());
		// Update partner_KPI_rfm_windowed
		try {
			boolean update = false;

			// Get latest summary
			ResultSet rs = session.execute("SELECT * FROM "+partnerName+".partner_KPI_rfm_windowed where termcode="+KPICustomerSummary.getTermcode()+" limit 1;");
			Row row = rs.one();
			if (row == null) {
				KPIPartnerSummary.setRecencyEarliest(KPICustomerSummary.getRecency());
				KPIPartnerSummary.setRecencyLatest(KPICustomerSummary.getRecency());
				KPIPartnerSummary.setFrequencyLeast(KPICustomerSummary.getFrequency());
				KPIPartnerSummary.setFrequencyMost(KPICustomerSummary.getFrequency());
				KPIPartnerSummary.setMonetaryLeast(KPICustomerSummary.getMonetary());
				KPIPartnerSummary.setMonetaryMost(KPICustomerSummary.getMonetary());
				update = true;
			} else {
				KPIPartnerSummary.setRecencyEarliest(row.getInt("recency_earliest"));
				KPIPartnerSummary.setRecencyLatest(row.getInt("recency_latest"));
				KPIPartnerSummary.setRecencyBucket2Floor(row.getInt("recency_bucket_2_floor"));
				KPIPartnerSummary.setRecencyBucket3Floor(row.getInt("recency_bucket_3_floor"));
				KPIPartnerSummary.setFrequencyLeast(row.getLong("frequency_least"));
				KPIPartnerSummary.setFrequencyMost(row.getLong("frequency_most"));
				KPIPartnerSummary.setFrequencyBucket2Floor(row.getLong("frequency_bucket_2_floor"));
				KPIPartnerSummary.setFrequencyBucket3Floor(row.getLong("frequency_bucket_3_floor"));
				KPIPartnerSummary.setMonetaryLeast(row.getDouble("monetary_least"));
				KPIPartnerSummary.setMonetaryMost(row.getDouble("monetary_most"));
				KPIPartnerSummary.setMonetaryBucket2Floor(row.getDouble("monetary_bucket_2_floor"));
				KPIPartnerSummary.setMonetaryBucket3Floor(row.getDouble("monetary_bucket_3_floor"));

				if (KPICustomerSummary.getRecency() < KPIPartnerSummary.getRecencyEarliest()) {
					KPIPartnerSummary.setRecencyEarliest(KPICustomerSummary.getRecency());
					update = calculateRecencyBuckets(KPIPartnerSummary);
				} else if (KPICustomerSummary.getRecency() > KPIPartnerSummary.getRecencyLatest()) {
					KPIPartnerSummary.setRecencyLatest(KPICustomerSummary.getRecency());
					update = calculateRecencyBuckets(KPIPartnerSummary);
				}
				if (KPICustomerSummary.getFrequency() < KPIPartnerSummary.getFrequencyLeast()) {
					KPIPartnerSummary.setFrequencyLeast(KPICustomerSummary.getFrequency());
					calculateFrequencyBuckets(KPIPartnerSummary); update = true;
				} else if (KPICustomerSummary.getFrequency() > KPIPartnerSummary.getFrequencyMost()) {
					KPIPartnerSummary.setFrequencyMost(KPICustomerSummary.getFrequency());
					calculateFrequencyBuckets(KPIPartnerSummary); update = true;
				}
				if (KPICustomerSummary.getMonetary() < KPIPartnerSummary.getMonetaryLeast()) {
					KPIPartnerSummary.setMonetaryLeast(KPICustomerSummary.getMonetary());
					calculateMonetaryBuckets(KPIPartnerSummary); update = true;
				} else if (KPICustomerSummary.getMonetary() > KPIPartnerSummary.getMonetaryMost()) {
					KPIPartnerSummary.setMonetaryMost(KPICustomerSummary.getMonetary());
					calculateMonetaryBuckets(KPIPartnerSummary); update = true;
				}
			}

			if (update) {
				session.execute("UPDATE " + partnerName + ".partner_KPI_rfm_windowed SET window_days = " + KPIPartnerSummary.getWindowDays() + ", recency_earliest = " + KPIPartnerSummary.getRecencyEarliest() + ", recency_latest = " + KPIPartnerSummary.getRecencyLatest() + ", recency_bucket_2_floor = " + KPIPartnerSummary.getRecencyBucket2Floor() + ", recency_bucket_3_floor = " + KPIPartnerSummary.getRecencyBucket3Floor() + ", frequency_least = " + KPIPartnerSummary.getFrequencyLeast() + ", frequency_most = " + KPIPartnerSummary.getFrequencyMost() + ", frequency_bucket_2_floor =" + KPIPartnerSummary.getFrequencyBucket2Floor() + ", frequency_bucket_3_floor = " + KPIPartnerSummary.getFrequencyBucket3Floor() + ", monetary_least = " + KPIPartnerSummary.getMonetaryLeast() + ", monetary_most = " + KPIPartnerSummary.getMonetaryMost() + ", monetary_bucket_2_floor = " + KPIPartnerSummary.getMonetaryBucket2Floor() + ", monetary_bucket_3_floor = " + KPIPartnerSummary.getMonetaryBucket3Floor() +" WHERE termcode = " + KPIPartnerSummary.getTermcode() + " AND date = " + KPIPartnerSummary.getDate() + ";");
			}

		} catch (Exception e) {
			LOG.error(SummarizePartnerRFMBolt.class.getSimpleName() + " error: " + e);
			outputCollector.reportError(e);
		}

		outputCollector.ack(tuple);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		//
	}

	private static boolean calculateRecencyBuckets(KPIPartnerSummary KPIPartnerSummary) throws Exception {
		boolean updated = false;
		if (KPIPartnerSummary.getRecencyEarliest() > 0) { // Must be a
			Calendar recencyLatestCal = Calendar.getInstance();
			recencyLatestCal.setTime(DateUtility.intToDate(KPIPartnerSummary.getRecencyLatest()));
			long recencyWindowLatest = recencyLatestCal.getTimeInMillis();

			Calendar recencyEarliestCal = Calendar.getInstance();
			recencyEarliestCal.setTime(DateUtility.intToDate(KPIPartnerSummary.getRecencyEarliest()));
			long recencyWindowEarliest = recencyEarliestCal.getTimeInMillis();

			int numberDaysDiffRecency = (int) TimeUnit.MILLISECONDS.toDays(Math.abs(recencyWindowLatest - recencyWindowEarliest));
			int numberDaysPerBucket = (int) Math.floor(numberDaysDiffRecency / 3);
			recencyEarliestCal.add(Calendar.DATE, numberDaysPerBucket);
			KPIPartnerSummary.setRecencyBucket2Floor(DateUtility.dateToInt(recencyEarliestCal.getTime()));

			recencyEarliestCal.add(Calendar.DATE, numberDaysPerBucket);
			KPIPartnerSummary.setRecencyBucket3Floor(DateUtility.dateToInt(recencyEarliestCal.getTime()));
			updated = true;
		}
		return updated;
	}

	private static void calculateFrequencyBuckets(KPIPartnerSummary KPIPartnerSummary) throws Exception {
		long frequencyRange = KPIPartnerSummary.getFrequencyMost() - KPIPartnerSummary.getFrequencyLeast();
		long frequencyPerBucket = (long)Math.floor(frequencyRange / 3);
		KPIPartnerSummary.setFrequencyBucket2Floor(KPIPartnerSummary.getFrequencyLeast() + frequencyPerBucket);
		KPIPartnerSummary.setFrequencyBucket3Floor(KPIPartnerSummary.getFrequencyBucket2Floor() + frequencyPerBucket);
	}

	private static void calculateMonetaryBuckets(KPIPartnerSummary KPIPartnerSummary) throws Exception {
		double monetaryRange = KPIPartnerSummary.getMonetaryMost() - KPIPartnerSummary.getMonetaryLeast();
		double monetaryPerBucket = Math.floor(monetaryRange / 3);
		KPIPartnerSummary.setMonetaryBucket2Floor(KPIPartnerSummary.getMonetaryLeast() + monetaryPerBucket);
		KPIPartnerSummary.setMonetaryBucket3Floor(KPIPartnerSummary.getMonetaryBucket2Floor() + monetaryPerBucket);
	}
}
