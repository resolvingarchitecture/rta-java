package ra.rta.services.business.events;

import java.util.ArrayList;
import java.util.List;

import ra.rta.models.ClassifiableEvent;
import ra.rta.models.Classifier;
import ra.rta.models.Customer;
import ra.rta.models.EnrichableEvent;
import ra.rta.models.Enricher;
import ra.rta.models.Envelope;
import ra.rta.services.business.tasks.ADIDEnricher;
import ra.rta.services.business.tasks.CustomerEnricher;

public class CustomerRegistrationEvent extends BaseEvent implements ClassifiableEvent, EnrichableEvent {

	private static final long serialVersionUID = 1L;

	private Customer customer;

	public CustomerRegistrationEvent() {
	}

	public CustomerRegistrationEvent(Envelope envelope) {
		super(envelope);
		customer = envelope.getBody().getRecords().get(0).getCustomer();
		setEntity(customer);
	}

	@Override
	public List<Classifier> getClassifiers() {
		List<Classifier> classifiers = new ArrayList<>();
//		classifiers.add(new CustomerKPIClassifier());
		return classifiers;
	}

	@Override
	public List<Enricher> getEnrichers() {
		List<Enricher> enrichers = new ArrayList<>();
		enrichers.add(new ADIDEnricher());
		enrichers.add(new CustomerEnricher());
		return enrichers;
	}

	@Override
	protected void setEnvelope(Envelope envelope) {
		super.setEnvelope(envelope);
		customer = envelope.getBody().getRecords().get(0).getCustomer();
		setEntity(customer);
	}

	public Customer getCustomer() {
		return customer;
	}
}
