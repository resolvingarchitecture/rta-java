package ra.rta.rfm.conspref.services;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import ra.rta.rfm.conspref.models.KPICustomerSummary;
import ra.rta.rfm.conspref.models.KPIGroupSummary;

public class RFMSummaryDataService extends BaseDataService {

    RFMSummaryDataService(Session session) {
        super(session);
    }

    public KPIGroupSummary getLatestKPIPartnerSummary(int gId, int termcode) {
        KPIGroupSummary summary = new KPIGroupSummary();
        summary.gId = gId;
        summary.tCode = termcode;
        ResultSet rs = session.execute(new SimpleStatement("SELECT * FROM group_KPI_rfm_windowed WHERE gId="+gId+" AND termcode = " + termcode + " limit 1;"));
        Row row = rs.one();
        if (row != null) {
            summary.date = row.getLong("date");
            summary.windowDays = row.getInt("window_days");
            summary.recencyEarliest = row.getLong("recency_earliest");
            summary.recencyLatest = row.getLong("recency_latest");
            summary.recencyBucket2Floor = row.getLong("recency_bucket_2_floor");
            summary.recencyBucket3Floor = row.getLong("recency_bucket_3_floor");
            summary.frequencyLeast = row.getLong("frequency_least");
            summary.frequencyMost = row.getLong("frequency_most");
            summary.frequencyBucket2Floor = row.getLong("frequency_bucket_2_floor");
            summary.frequencyBucket3Floor = row.getLong("frequency_bucket_3_floor");
            summary.monetaryLeast = row.getDouble("monetary_least");
            summary.monetaryMost = row.getDouble("monetary_most");
            summary.monetaryBucket2Floor = row.getDouble("monetary_bucket_2_floor");
            summary.monetaryBucket3Floor = row.getDouble("monetary_bucket_3_floor");
        }
        return summary;
    }

    public KPIGroupSummary getKPIPartnerSummaryByDate(int gId, int termcode, long date) {
        KPIGroupSummary summary = new KPIGroupSummary();
        summary.gId = gId;
        summary.tCode = termcode;
        summary.date = date;
        ResultSet rs = session.execute(new SimpleStatement("SELECT * FROM group_KPI_rfm_windowed WHERE gId="+gId+" AND termcode = " + termcode + " AND date = "+date+";"));
        Row row = rs.one();
        if (row != null) {
            summary.windowDays = row.getInt("window_days");
            summary.recencyEarliest = row.getLong("recency_earliest");
            summary.recencyLatest = row.getLong("recency_latest");
            summary.recencyBucket2Floor = row.getLong("recency_bucket_2_floor");
            summary.recencyBucket3Floor = row.getLong("recency_bucket_3_floor");
            summary.frequencyLeast = row.getLong("frequency_least");
            summary.frequencyMost = row.getLong("frequency_most");
            summary.frequencyBucket2Floor = row.getLong("frequency_bucket_2_floor");
            summary.frequencyBucket3Floor = row.getLong("frequency_bucket_3_floor");
            summary.monetaryLeast = row.getDouble("monetary_least");
            summary.monetaryMost = row.getDouble("monetary_most");
            summary.monetaryBucket2Floor = row.getDouble("monetary_bucket_2_floor");
            summary.monetaryBucket3Floor = row.getDouble("monetary_bucket_3_floor");
        }
        return summary;
    }

    public KPICustomerSummary getLatestKPICustomerSummary(int gId, int cId, int termcode) {
        KPICustomerSummary summary = new KPICustomerSummary();
        summary.gId = gId;
        summary.cId = cId;
        summary.tCode = termcode;
        ResultSet rs = session.execute(new SimpleStatement("SELECT date, frequency, monetary, recency FROM customer_KPI_rfm_windowed WHERE gId="+gId+" AND  cId = " + cId + " AND termcode = " + termcode + " limit 1;"));
        Row row = rs.one();
        if (row != null) {
            summary.date = row.getLong("date");
            summary.recency = row.getLong("recency");
            summary.frequency = row.getLong("frequency");
            summary.monetary = row.getDouble("monetary");
        }
        return summary;
    }

    public KPICustomerSummary getKPICustomerSummaryByDate(int gId, int cId, int termcode, long date) {
        KPICustomerSummary summary = new KPICustomerSummary();
        summary.gId = gId;
        summary.cId = cId;
        summary.tCode = termcode;
        summary.date = date;
        summary.recency = date;
        ResultSet rs = session.execute(new SimpleStatement("SELECT frequency, monetary FROM customer_KPI_rfm_windowed WHERE gId="+gId+" AND  cId = " + cId + " AND termcode = " + termcode + " and date = " + date + ";"));
        Row row = rs.one();
        if (row != null) {
            summary.frequency = row.getLong("frequency");
            summary.monetary = row.getDouble("monetary");
        }
        return summary;
    }
}
