package ra.rta.rfm.conspref.models;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Date;

public class CustomerInteraction {

	@SuppressWarnings("hiding")
	static final long serialVersionUID = 1L;

    public Customer customer;
    public String reltype;
    public String type;
    public String channel;
    public Date dateTime;
    public String memo;

    @Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
