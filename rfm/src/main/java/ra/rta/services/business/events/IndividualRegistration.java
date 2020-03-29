package ra.rta.services.business.events;

import java.util.ArrayList;
import java.util.List;

import ra.rta.classify.Classifiable;
import ra.rta.classify.Classifier;
import ra.rta.enrich.Enrichable;
import ra.rta.enrich.Enricher;
import ra.rta.models.*;
import ra.rta.models.Individual;
import ra.rta.services.business.tasks.ADIDEnricher;
import ra.rta.enrich.IndividualEnricher;
import ra.rta.classify.IndividualKPIClassifier;

public class IndividualRegistration extends Event implements Classifiable, Enrichable {

	private static final long serialVersionUID = 1L;

	public Individual individual;

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
