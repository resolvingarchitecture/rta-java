package ra.rta.services.business.events;

import ra.rta.models.Envelope;
import ra.rta.models.LineAccount;

public class LineAccountUpdateEvent extends AccountEvent {

	private static final long serialVersionUID = 1L;

	public LineAccountUpdateEvent() {
	}

	public LineAccountUpdateEvent(Envelope envelope) {
		super(envelope);
		account = new LineAccount();
        setEntity(account);
	}

	@Override
	protected void setEnvelope(Envelope envelope) {
		super.setEnvelope(envelope);
        account = new LineAccount();
        setEntity(account);
	}

}
