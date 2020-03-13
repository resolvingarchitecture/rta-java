package ra.rta.services.business.events;

import java.util.ArrayList;
import java.util.List;

import ra.rta.models.*;
import ra.rta.services.business.tasks.DepositTransactionEnricher;
import ra.rta.services.business.tasks.TransactionClassifier;

/**
 *
 */
public abstract class TransactionEvent extends BaseEvent implements ClassifiableEvent, EnrichableEvent {

	private static final long serialVersionUID = 1L;

    protected Transaction transaction;

	public TransactionEvent() {
	}

	public TransactionEvent(Envelope envelope) {
		super(envelope);
	}

	@Override
	public List<Classifier> getClassifiers() {
		List<Classifier> classifiers = new ArrayList<>();
		classifiers.add(new TransactionClassifier());
		return classifiers;
	}

	@Override
	public List<Enricher> getEnrichers() {
		List<Enricher> enrichers = new ArrayList<>();
		enrichers.add(new DepositTransactionEnricher());
		return enrichers;
	}

    public Transaction getTransaction() {
        return transaction;
    }

}
