package ra.rta.services.business.events;

import ra.rta.models.CardAccount;
import ra.rta.models.Envelope;

public class CardAccountUpdateEvent extends AccountEvent {

	private static final long serialVersionUID = 1L;

	public CardAccountUpdateEvent() {
	}

	public CardAccountUpdateEvent(Envelope envelope) {
		super(envelope);
		account = new CardAccount();
		setEntity(account);
	}

	@Override
	protected void setEnvelope(Envelope envelope) {
		super.setEnvelope(envelope);
        account = new CardAccount();
        setEntity(account);
	}

}
