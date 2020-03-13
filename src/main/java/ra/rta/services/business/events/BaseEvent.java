package ra.rta.services.business.events;

import java.util.UUID;

import ra.rta.models.Entity;
import ra.rta.models.Envelope;
import ra.rta.models.Event;

public abstract class BaseEvent implements Event {

	private static final long serialVersionUID = 1L;

	protected UUID id;
	protected Envelope envelope;
	protected Entity entity;

	public BaseEvent() {
		id = UUID.randomUUID();
	}

	public BaseEvent(Envelope envelope) {
		this();
		this.envelope = envelope;
	}

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public Envelope getEnvelope() {
		return envelope;
	}

	protected void setEnvelope(Envelope envelope) {
		this.envelope = envelope;
	}

	@Override
	public Entity getEntity() {
		return entity;
	}

	protected void setEntity(Entity entity) {
		this.entity = entity;
	}
}
