package ra.rta;

import ra.rta.Event;
import ra.rta.utilities.RandomUtil;

import java.util.Arrays;
import java.util.Date;

/**
 *
 */
public class EventException extends Exception {

	private static final long serialVersionUID = 1L;

	public long id;
	public long timestamp;
	public String component;
	public int code;
	public String message;
	public Event event;

	public EventException(String component, int code, String message, Event event) {
		super(message);
		id = RandomUtil.nextRandomLong();
		timestamp = new Date().getTime();
        this.code = code;
		this.component = component;
		this.message = message;
		this.event = event;
	}

	public EventException(String component, int code, String message, Event event, Throwable throwable) {
		this(component, code, message, event);
		this.message += "; Stack: "+ Arrays.toString(throwable.getStackTrace())+"; Throwable Localized Message: "+throwable.getLocalizedMessage();
	}
}
