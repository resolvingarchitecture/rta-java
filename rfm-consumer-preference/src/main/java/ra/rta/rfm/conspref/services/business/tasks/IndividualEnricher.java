package ra.rta.rfm.conspref.services.business.tasks;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.LocalDate;
import org.joda.time.Months;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.models.Event;
import ra.rta.rfm.conspref.models.*;

/**
 *
 */
public class IndividualEnricher implements Enricher {

	private static final Logger LOG = LoggerFactory.getLogger(IndividualEnricher.class);

	@Override
	public void enrich(Enrichable enrichable) throws Exception {
		Customer customer = null;
		if(enrichable instanceof ra.rta.models.Event) {
			ra.rta.models.Event event = (Event)enrichable;
			if(event.payload.get("individual")!=null) {
				customer = (Customer) event.payload.get("individual");
			}
		}
		if(customer ==null) {
			LOG.warn("No Individual found in Enrichable; unable to enrich.");
			return;
		}
		if (customer.birthYear > 1900 && (customer.age < 0 || customer.age > 200)) {
			customer.age = Calendar.getInstance().get(Calendar.YEAR) - customer.birthYear;
			customer.save = true;
		}
		if (customer.openDateMonths == null && customer.openDate != null) {
			customer.openDateMonths =
					Months.monthsBetween(
							new LocalDate(customer.openDate).withDayOfMonth(1),
							new LocalDate(new Date()).withDayOfMonth(1)
					).getMonths();
			customer.save = true;
		}
	}
}
