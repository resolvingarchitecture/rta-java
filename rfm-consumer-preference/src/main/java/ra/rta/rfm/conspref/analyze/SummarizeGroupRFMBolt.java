package ra.rta.rfm.conspref.analyze;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import ra.rta.connectors.cassandra.CassandraMgr;
import ra.rta.rfm.conspref.models.KPICustomerSummary;
import ra.rta.rfm.conspref.models.KPIGroupSummary;
import ra.rta.rfm.conspref.utilities.DateUtility;

/**
 * Sums each supplied Group KPI's frequency and monetary and determines the most recent time across its window.
 */
public class SummarizeGroupRFMBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(SummarizeGroupRFMBolt.class);

	private OutputCollector outputCollector;

	private Session session;

	@Override
	@SuppressWarnings("hiding")
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.outputCollector = outputCollector;
		// Cassandra preparation
		session = CassandraMgr.init(map).getSession();
	}

	@Override
	public void execute(Tuple tuple) {
		int gId = tuple.getIntegerByField("groupId");
		int termcode = tuple.getIntegerByField("termcode");
		KPICustomerSummary kpiCustomerSummary = (KPICustomerSummary)tuple.getValueByField("summary");
		KPIGroupSummary kpiGroupSummary = new KPIGroupSummary();
		kpiGroupSummary.tCode = kpiCustomerSummary.tCode;
		kpiGroupSummary.date = kpiCustomerSummary.date;
		kpiGroupSummary.windowDays = kpiCustomerSummary.windowDays;
		// Update partner_KPI_rfm_windowed
		try {
			boolean update = false;

			// Get latest summary
			ResultSet rs = session.execute("SELECT * FROM group_KPI_rfm_windowed where termcode="+ kpiCustomerSummary.tCode+" limit 1;");
			Row row = rs.one();
			if (row == null) {
				kpiGroupSummary.recencyEarliest = kpiCustomerSummary.recency;
				kpiGroupSummary.recencyLatest = kpiCustomerSummary.recency;
				kpiGroupSummary.frequencyLeast = kpiCustomerSummary.frequency;
				kpiGroupSummary.frequencyMost = kpiCustomerSummary.frequency;
				kpiGroupSummary.monetaryLeast = kpiCustomerSummary.monetary;
				kpiGroupSummary.monetaryMost = kpiCustomerSummary.monetary;
				update = true;
			} else {
				kpiGroupSummary.recencyEarliest = row.getInt("recency_earliest");
				kpiGroupSummary.recencyLatest = row.getInt("recency_latest");
				kpiGroupSummary.recencyBucket2Floor = row.getInt("recency_bucket_2_floor");
				kpiGroupSummary.recencyBucket3Floor = row.getInt("recency_bucket_3_floor");
				kpiGroupSummary.frequencyLeast = row.getLong("frequency_least");
				kpiGroupSummary.frequencyMost = row.getLong("frequency_most");
				kpiGroupSummary.frequencyBucket2Floor = row.getLong("frequency_bucket_2_floor");
				kpiGroupSummary.frequencyBucket3Floor = row.getLong("frequency_bucket_3_floor");
				kpiGroupSummary.monetaryLeast = row.getDouble("monetary_least");
				kpiGroupSummary.monetaryMost = row.getDouble("monetary_most");
				kpiGroupSummary.monetaryBucket2Floor = row.getDouble("monetary_bucket_2_floor");
				kpiGroupSummary.monetaryBucket3Floor = row.getDouble("monetary_bucket_3_floor");

				if (kpiCustomerSummary.recency < kpiGroupSummary.recencyEarliest) {
					kpiGroupSummary.recencyEarliest = kpiCustomerSummary.recency;
					update = calculateRecencyBuckets(kpiGroupSummary);
				} else if (kpiCustomerSummary.recency > kpiGroupSummary.recencyLatest) {
					kpiGroupSummary.recencyLatest = kpiCustomerSummary.recency;
					update = calculateRecencyBuckets(kpiGroupSummary);
				}
				if (kpiCustomerSummary.frequency < kpiGroupSummary.frequencyLeast) {
					kpiGroupSummary.frequencyLeast = kpiCustomerSummary.frequency;
					calculateFrequencyBuckets(kpiGroupSummary); update = true;
				} else if (kpiCustomerSummary.frequency > kpiGroupSummary.frequencyMost) {
					kpiGroupSummary.frequencyMost = kpiCustomerSummary.frequency;
					calculateFrequencyBuckets(kpiGroupSummary); update = true;
				}
				if (kpiCustomerSummary.monetary < kpiGroupSummary.monetaryLeast) {
					kpiGroupSummary.monetaryLeast = kpiCustomerSummary.monetary;
					calculateMonetaryBuckets(kpiGroupSummary); update = true;
				} else if (kpiCustomerSummary.monetary > kpiGroupSummary.monetaryMost) {
					kpiGroupSummary.monetaryMost = kpiCustomerSummary.monetary;
					calculateMonetaryBuckets(kpiGroupSummary); update = true;
				}
			}

			if (update) {
				session.execute("UPDATE group_KPI_rfm_windowed SET window_days = " + kpiGroupSummary.windowDays + ", recency_earliest = " + kpiGroupSummary.recencyEarliest + ", recency_latest = " + kpiGroupSummary.recencyLatest + ", recency_bucket_2_floor = " + kpiGroupSummary.recencyBucket2Floor + ", recency_bucket_3_floor = " + kpiGroupSummary.recencyBucket3Floor + ", frequency_least = " + kpiGroupSummary.frequencyLeast + ", frequency_most = " + kpiGroupSummary.frequencyMost + ", frequency_bucket_2_floor =" + kpiGroupSummary.frequencyBucket2Floor + ", frequency_bucket_3_floor = " + kpiGroupSummary.frequencyBucket3Floor + ", monetary_least = " + kpiGroupSummary.monetaryLeast + ", monetary_most = " + kpiGroupSummary.monetaryMost + ", monetary_bucket_2_floor = " + kpiGroupSummary.monetaryBucket2Floor + ", monetary_bucket_3_floor = " + kpiGroupSummary.monetaryBucket3Floor +" WHERE gId = "+gId+" AND termcode = " + kpiGroupSummary.tCode + " AND date = " + kpiGroupSummary.date + ";");
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

	private static boolean calculateRecencyBuckets(KPIGroupSummary sum) throws Exception {
		if (sum.recencyEarliest > 0) { // Must be a positive
			long diffRecency = Math.abs(sum.recencyLatest - sum.recencyEarliest);
			long spreadPerBucket = (long)Math.floor((double)diffRecency / 3);
			sum.recencyBucket2Floor = sum.recencyEarliest + spreadPerBucket;
			sum.recencyBucket3Floor = sum.recencyBucket2Floor + spreadPerBucket;
			return true;
		}
		return false;
	}

	private static void calculateFrequencyBuckets(KPIGroupSummary sum) throws Exception {
		long frequencyRange = sum.frequencyMost - sum.frequencyLeast;
		long spreadPerBucket = (long)Math.floor((double)frequencyRange / 3);
		sum.frequencyBucket2Floor = sum.frequencyLeast + spreadPerBucket;
		sum.frequencyBucket3Floor = sum.frequencyBucket2Floor + spreadPerBucket;
	}

	private static void calculateMonetaryBuckets(KPIGroupSummary sum) throws Exception {
		double monetaryRange = sum.monetaryMost - sum.monetaryLeast;
		double spreadPerBucket = Math.floor(monetaryRange / 3);
		sum.monetaryBucket2Floor = sum.monetaryLeast + spreadPerBucket;
		sum.monetaryBucket3Floor = sum.monetaryBucket2Floor + spreadPerBucket;
	}
}
