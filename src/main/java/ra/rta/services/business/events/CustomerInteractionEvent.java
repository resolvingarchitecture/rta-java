package ra.rta.services.business.events;

import ra.rta.models.Customer;
import ra.rta.models.Envelope;

public class CustomerInteractionEvent extends BaseEvent {

	private static final long serialVersionUID = 1L;

	private Customer customer;

	public CustomerInteractionEvent() {
	}

	public CustomerInteractionEvent(Envelope envelope) {
		super(envelope);
		customer = envelope.getBody().getRecords().get(0).getCustomer();
		setEntity(customer);
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
