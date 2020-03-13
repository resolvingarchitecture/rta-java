package ra.rta.services.business.events;

import ra.rta.models.Envelope;
import ra.rta.models.LoanTransaction;

public class LoanTransactionEvent extends TransactionEvent {

	private static final long serialVersionUID = 1L;

	public LoanTransactionEvent() {
	}

	public LoanTransactionEvent(Envelope envelope) {
		super(envelope);
		transaction = new LoanTransaction();
        setEntity(transaction);
	}

	@Override
	protected void setEnvelope(Envelope envelope) {
		super.setEnvelope(envelope);
        transaction = new LoanTransaction();
        setEntity(transaction);
	}

}
