package ra.rta.models;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Date;

public class IndividualInteraction {

	@SuppressWarnings("hiding")
	static final long serialVersionUID = 1L;

    public Individual individual;
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
