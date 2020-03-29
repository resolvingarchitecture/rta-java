package ra.rta.rfm.conspref.services.business.events;

import java.util.ArrayList;
import java.util.List;

import ra.rta.models.Event;
import ra.rta.rfm.conspref.models.Customer;
import ra.rta.rfm.conspref.services.business.tasks.ADIDEnricher;
import ra.rta.rfm.conspref.services.business.tasks.IndividualEnricher;
import ra.rta.rfm.conspref.services.business.tasks.IndividualKPIClassifier;

public class IndividualRegistration extends Event implements Classifiable, Enrichable {

	private static final long serialVersionUID = 1L;

	public Customer customer;

	public IndividualRegistration() {
	}

	@Override
	public List<Classifier> getClassifiers() {
		List<Classifier> classifiers = new ArrayList<>();
		classifiers.add(new IndividualKPIClassifier());
		return classifiers;
	}

	@Override
	public List<Enricher> getEnrichers() {
		List<Enricher> enrichers = new ArrayList<>();
		enrichers.add(new ADIDEnricher());
		enrichers.add(new IndividualEnricher());
		return enrichers;
	}

}
