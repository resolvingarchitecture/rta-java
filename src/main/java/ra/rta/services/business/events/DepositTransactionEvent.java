package ra.rta.services.business.events;

import ra.rta.models.DepositTransaction;
import ra.rta.models.Envelope;

public class DepositTransactionEvent extends TransactionEvent {

	private static final long serialVersionUID = 1L;

	public DepositTransactionEvent() {
	}

	public DepositTransactionEvent(Envelope envelope) {
		super(envelope);
		transaction = new DepositTransaction();
		setEntity(transaction);
	}

	@Override
	protected void setEnvelope(Envelope envelope) {
		super.setEnvelope(envelope);
        transaction = new DepositTransaction();
        setEntity(transaction);
	}

}
