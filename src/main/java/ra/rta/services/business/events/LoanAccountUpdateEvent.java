package ra.rta.services.business.events;

import ra.rta.models.Envelope;
import ra.rta.models.LoanAccount;

public class LoanAccountUpdateEvent extends AccountEvent {

	private static final long serialVersionUID = 1L;

	public LoanAccountUpdateEvent() {
	}

	public LoanAccountUpdateEvent(Envelope envelope) {
		super(envelope);
		account = new LoanAccount();
        setEntity(account);
	}

	@Override
	protected void setEnvelope(Envelope envelope) {
		super.setEnvelope(envelope);
        account = new LoanAccount();
        setEntity(account);
	}

}
