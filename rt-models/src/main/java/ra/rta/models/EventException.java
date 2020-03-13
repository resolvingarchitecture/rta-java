package ra.rta.models;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 *
 */
public class EventException extends Exception {

	private static final long serialVersionUID = 1L;

	private UUID id;
	private Date timestamp;
	private String component;
    private int code;
	private String message;
	private Event event;

	public EventException(String component, int code, String message, Event event) {
		super(message);
		id = UUID.randomUUID();
		timestamp = new Date();
        this.code = code;
		this.component = component;
		this.message = message;
		this.event = event;
	}

	public EventException(String component, int code, String message, Event event, Throwable throwable) {
		this(component, code, message, event);
		this.message += "; Stack: "+ Arrays.toString(throwable.getStackTrace())+"; Throwable Localized Message: "+throwable.getLocalizedMessage();
	}

	public UUID getId() {
		return id;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getComponent() {
		return component;
	}

    public int getCode() {
        return code;
    }

    @Override
	public String getMessage() {
		return message;
	}

	public Event getEvent() {
		return event;
	}
}
