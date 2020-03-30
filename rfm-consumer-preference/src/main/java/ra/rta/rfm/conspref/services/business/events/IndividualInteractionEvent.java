package ra.rta.rfm.conspref.services.business.events;

import ra.rta.Event;
import ra.rta.rfm.conspref.models.CustomerInteraction;

public class IndividualInteractionEvent extends Event {

	static final long serialVersionUID = 1L;

	public CustomerInteraction interaction;

}
