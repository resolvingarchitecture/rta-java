package ra.rta.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 *
 */
public class Record implements Serializable {

	private static final long serialVersionUID = 1L;

	private String raw;
	private String rawEncoding;
	private Partner partner = new Partner();
	private Customer customer = null;
	private Transaction transaction;
	private List<EventException> eventErrors = new ArrayList<>();
	private boolean transformed = false;
	private String json;
	private Integer tried = 0;

	public String getRaw() {
		return raw;
	}

	public void setRaw(String raw) {
		this.raw = raw;
	}

	public String getRawEncoding() {
		return rawEncoding;
	}

	public void setRawEncoding(String rawEncoding) {
		this.rawEncoding = rawEncoding;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public List<EventException> getEventErrors() {
		return eventErrors;
	}

	public void setEventErrors(List<EventException> eventErrors) {
		this.eventErrors = eventErrors;
	}

	public boolean getTransformed() {
		return transformed;
	}

	public void setTransformed(boolean transformed) {
		this.transformed = transformed;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public Integer getTried() {
		return tried;
	}

	public void setTried(Integer tried) {
		this.tried = tried;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
