package ra.rta.services.business.tasks;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.LocalDate;
import org.joda.time.Months;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.enrich.Enrichable;
import ra.rta.enrich.Enricher;
import ra.rta.models.*;

/**
 *
 */
public class IndividualEnricher implements Enricher {

	private static final Logger LOG = LoggerFactory.getLogger(ra.rta.enrich.IndividualEnricher.class);

	@Override
	public void enrich(Enrichable enrichable) throws Exception {
		Individual individual = null;
		if(enrichable instanceof Event) {
			Event event = (Event)enrichable;
			if(event.payload.get("individual")!=null) {
				individual = (Individual) event.payload.get("individual");
			}
		}
		if(individual==null) {
			LOG.warn("No Individual found in Enrichable; unable to enrich.");
			return;
		}
		if (individual.birthYear > 1900 && (individual.age < 0 || individual.age > 200)) {
			individual.age = Calendar.getInstance().get(Calendar.YEAR) - individual.birthYear;
			individual.save = true;
		}
		if (individual.openDateMonths == null && individual.openDate != null) {
			individual.openDateMonths =
					Months.monthsBetween(
							new LocalDate(individual.openDate).withDayOfMonth(1),
							new LocalDate(new Date()).withDayOfMonth(1)
					).getMonths();
			individual.save = true;
		}
	}
}
