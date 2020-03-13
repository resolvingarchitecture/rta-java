package ra.rta.services.business.events;

import ra.rta.models.CardTransaction;
import ra.rta.models.Envelope;

public class CardTransactionEvent extends TransactionEvent {

	private static final long serialVersionUID = 1L;

	public CardTransactionEvent() {}

	public CardTransactionEvent(Envelope envelope) {
		super(envelope);
		transaction = new CardTransaction();
        setEntity(transaction);
	}

	@Override
	protected void setEnvelope(Envelope envelope) {
		super.setEnvelope(envelope);
        transaction = new CardTransaction();
        setEntity(transaction);
	}

}
