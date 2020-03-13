package ra.rta.services.business.events;

import ra.rta.models.Account;
import ra.rta.models.Envelope;

public abstract class AccountEvent extends BaseEvent {

	private static final long serialVersionUID = 1L;

	protected Account account;

	public AccountEvent() {
	}

	public AccountEvent(Envelope envelope) {
		super(envelope);
	}

	public Account getAccount() {
		return account;
	}
}
