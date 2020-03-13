package ra.rta.services.business.events;

import ra.rta.models.DepositAccount;
import ra.rta.models.Envelope;

public class DepositAccountUpdateEvent extends AccountEvent {

	private static final long serialVersionUID = 1L;

	public DepositAccountUpdateEvent() {
	}

	public DepositAccountUpdateEvent(Envelope envelope) {
		super(envelope);
		account = new DepositAccount();
        setEntity(account);
	}

	@Override
	protected void setEnvelope(Envelope envelope) {
		super.setEnvelope(envelope);
        account = new DepositAccount();
        setEntity(account);
	}

}
