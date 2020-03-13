package ra.rta.services.business.tasks;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.LocalDate;
import org.joda.time.Months;

import ra.rta.models.Customer;
import ra.rta.models.EnrichableEvent;
import ra.rta.models.Enricher;
import ra.rta.models.Partner;

/**
 *
 */
public class CustomerEnricher implements Enricher {

	@Override
	public void enrich(EnrichableEvent event) throws Exception {
		// Calculate age if necessary
		Customer customer = (Customer)event.getEntity();
		Partner partner = customer.getPartner();
		customer.setPartner(partner); // Ensure Entity's Partner is set
		if (customer.getBirthYear() > 1900 && (customer.getAge() < 0 || customer.getAge() > 200)) {
			customer.setAge(Calendar.getInstance().get(Calendar.YEAR) - customer.getBirthYear());
		}
		if (customer.getOpenDateMonths() == null && customer.getOpenDate() != null
				&& !customer.getOpenDate().equals("10101") && !customer.getOpenDate().equals("99991231")) {
			// Calculate Customer.openDateMonths
			customer.setOpenDateMonths(
					Months.monthsBetween(
							new LocalDate(customer.getOpenDate()).withDayOfMonth(1),
							new LocalDate(new Date()).withDayOfMonth(1)
							).getMonths()
					);
			// Flag Customer for creation
			customer.setSave("create");
			//            LOG.warn("Customer.openDateMonths: "+customer.getOpenDateMonths());
		}
	}
}
