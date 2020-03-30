package ra.rta.rfm.conspref.services.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.datastax.driver.core.*;
import ra.rta.rfm.conspref.models.Customer;
import ra.rta.classify.KPI;
import ra.rta.rfm.conspref.utilities.DateUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class IndividualDataService extends BaseDataService {

    private static Logger LOG = LoggerFactory.getLogger(IndividualDataService.class);

	public IndividualDataService(Session session) {
		super(session);
	}

	public void saveKPI(Customer customer, boolean transactional) throws Exception {
        List<ResultSetFuture> futures = new ArrayList<>();
        ResultSetFuture future;
		for(KPI kpi : customer.kpis) {
			if (transactional) {
				int windowDaysLeft = kpi.windowDays - (int) DateUtility.daysDifference(Calendar.getInstance().getTime(), kpi.date);
				if (kpi.active && windowDaysLeft > 0) { // Only insert if made active and not already expired
					Calendar nowCal = Calendar.getInstance();
					nowCal.add(Calendar.SECOND, windowDaysLeft);
					String expiresDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(nowCal.getTime());
                    long ttl = windowDaysLeft * 24 * 60 * 60; // In seconds
                    // Add to windowed Customer KPI
					future = session.executeAsync(new SimpleStatement("INSERT INTO " + customer.group.name + ".individual_KPI_active (id, termcode, expires, window_days) VALUES ("
                            + customer.id + "," + kpi.termcode + ",'" + expiresDate + "', " + kpi.windowDays + ") USING TTL " + ttl + ";"));
                    futures.add(future);
				}

                // Update Cassandra frequency & monetary counters (recency assumed by transaction date used in counters)
                future = session.executeAsync(new SimpleStatement("UPDATE " + customer.group.name + ".individual_KPI_frequency SET frequency = frequency + 1 WHERE id = "+ kpi.id+" AND termcode = "+ kpi.termcode+" AND date = "+DateUtility.dateToInt(kpi.date)+";"));
                futures.add(future);
                future = session.executeAsync(new SimpleStatement("UPDATE " + customer.group.name + ".individual_KPI_monetary SET monetary = monetary + " + ((int)(kpi.monetary*100)) + " WHERE id = "+ kpi.id+" AND termcode = "+ kpi.termcode+" AND date = "+DateUtility.dateToInt(kpi.date)+";"));
                futures.add(future);
			} else {
                // Add to Individual KPI - non expiring
				future = session.executeAsync(new SimpleStatement("INSERT INTO " + customer.group.name + ".individual_KPI (id, termcode) VALUES (" + customer.id + "," + kpi.termcode + ");"));
			    futures.add(future);
            }
            // Add to historical Individual KPI
            future = session.executeAsync(new SimpleStatement("INSERT INTO " + customer.group.name + ".individual_KPI_history (id, termcode, transaction_date, KPI_type_id, recency, recency_earliest_global, recency_latest_global, recency_score, frequency, frequency_total, frequency_least_global, frequency_most_global, frequency_score, monetary, monetary_total, monetary_least_global, monetary_most_global, monetary_score, window_days, active) VALUES ("
                    + kpi.id + ", " + kpi.termcode+", '"+DateUtility.timestampToString(kpi.date)+"', " + kpi.type.ordinal() +", " + kpi.recency + ", " + kpi.groupSummary.recencyEarliest + ", " + kpi.groupSummary.recencyLatest + ", " + kpi.recencyScore + ", " + kpi.frequency + ", " + kpi.individualSummary.frequency + ", " + kpi.groupSummary.frequencyLeast + ", " + kpi.groupSummary.frequencyMost + ", " + kpi.frequencyScore + ", " + kpi.monetary + ", " + kpi.individualSummary.monetary + ", " + kpi.groupSummary.monetaryLeast + ", " + kpi.groupSummary.monetaryMost + ", " + kpi.monetaryScore + ", " + kpi.windowDays + ", " + kpi.active+");"));
		    futures.add(future);
        }
        for(ResultSetFuture rsf : futures) {
            rsf.getUninterruptibly();
        }
	}

	public void save(Customer customer) throws Exception {
		if (customer.save) {
			session.execute(new SimpleStatement("INSERT INTO " + customer.group.name
                    + ".individual (id, gId, open_date, open_date_months, close_date, process_date, bstatus, country, zip_code, age, birth_year, deceased_year, type, state, investable_assets, estatement_indicator, glba_opt_out, solicitation_code, segmentation, business_inception_date, business_sic_code, business_naics_code, business_annual_sales, business_num_employees, save) VALUES ("
                    + customer.id + ", '" + customer.gId + "', "+DateUtility.timestampToString(customer.openDate)+", "+ customer.openDateMonths+", "
                    +DateUtility.timestampToString(customer.closeDate)+", "+DateUtility.timestampToString(customer.processDate)+", "+ customer.status.ordinal()+", "
                    + customer.country+", '"+ customer.zipCode+"', "+ customer.age+", "+ customer.birthYear+", "
                    + customer.deceasedYear+", '"+ customer.type.ordinal()+"', '"+ customer.state+"', "
                    + customer.investableAssets+", "+ customer.estatementIndicator+", "
                    + customer.glbaOptOut+", "+ customer.solicitationCode+", "+ customer.segmentation+", "
                    +DateUtility.timestampToString(customer.businessInceptionDate)+", "+ customer.businessSicCode+", "+ customer.businessNaicsCode+", "
                    + customer.businessAnnualSales+", "+ customer.businessNumEmployees+", '"+ customer.save+"');"));
		}
	}

    public void loadIndividual(Customer customer, String processDate) throws Exception {
        ResultSet rs = session.execute(new SimpleStatement("SELECT * FROM "+ customer.group.name+".individual WHERE id="+ customer.id+" and process_date='" + processDate + "' limit 1;"));
        Row row = rs.one();
        if(row != null){
            customer.id = (row.getLong("id"));
            customer.openDate = new Date(row.getDate("open_date").getMillisSinceEpoch());
            customer.closeDate = new Date(row.getDate("close_date").getMillisSinceEpoch());
            customer.processDate = new Date(row.getDate("process_date").getMillisSinceEpoch());
            customer.status = Customer.Status.values()[(row.getInt("status"))];
            customer.country = row.getInt("country");
            customer.zipCode = row.getString("zip_code");
            customer.birthYear = row.getInt("birth_year");
            customer.deceasedYear = row.getInt("deceased_year");
            customer.type = Customer.Type.values()[(row.getInt("type"))];
            customer.state = row.getString("state");
            customer.investableAssets = row.getDouble("investable_assets");
            customer.estatementIndicator = row.getBool("estatement_indicator");
            customer.glbaOptOut = row.getBool("glba_opt_out");
            customer.solicitationCode = row.getBool("solicitation_code");
            customer.segmentation = row.getInt("segmentation");
            customer.businessInceptionDate = new Date(row.getDate("business_inception_date").getMillisSinceEpoch());
            customer.businessSicCode = row.getInt("business_sic_code");
            customer.businessNaicsCode = row.getInt("business_naics_code");
            customer.businessAnnualSales = row.getDouble("business_annual_sales");
            customer.businessNumEmployees = row.getInt("business_num_employees");
            customer.subType1 = row.getString("sub_type1");
            customer.subType2 = row.getString("sub_type2");
            customer.subType3 = row.getString("sub_type3");
        }
    }
}
