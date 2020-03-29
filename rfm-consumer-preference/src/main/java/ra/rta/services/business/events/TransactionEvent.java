package ra.rta.services.business.events;

import java.util.ArrayList;
import java.util.List;

import ra.rta.models.*;
import ra.rta.services.business.tasks.TransactionClassifier;

/**
 *
 */
public class TransactionEvent extends Event implements Classifiable, Enrichable {

	private static final long serialVersionUID = 1L;

    public FinancialTransaction financialTransaction;

	@Override
	public List<Classifier> getClassifiers() {
		List<Classifier> classifiers = new ArrayList<>();
		classifiers.add(new TransactionClassifier());
		return classifiers;
	}

	@Override
	public List<Enricher> getEnrichers() {
		List<Enricher> enrichers = new ArrayList<>();
		// add enrichers here
		return enrichers;
	}

    public FinancialTransaction getFinancialTransaction() {
        return financialTransaction;
    }

}
