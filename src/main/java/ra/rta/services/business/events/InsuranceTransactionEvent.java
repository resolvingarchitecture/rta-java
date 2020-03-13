package ra.rta.services.business.events;

import ra.rta.models.Envelope;
import ra.rta.models.InsuranceTransaction;

public class InsuranceTransactionEvent extends TransactionEvent {

	private static final long serialVersionUID = 1L;

	public InsuranceTransactionEvent() {
	}

	public InsuranceTransactionEvent(Envelope envelope) {
		super(envelope);
		transaction = new InsuranceTransaction();
        setEntity(transaction);
	}

	@Override
	protected void setEnvelope(Envelope envelope) {
		super.setEnvelope(envelope);
        transaction = new InsuranceTransaction();
        setEntity(transaction);
	}

}
