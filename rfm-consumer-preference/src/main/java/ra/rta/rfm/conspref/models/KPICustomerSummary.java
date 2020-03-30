package ra.rta.rfm.conspref.models;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class KPICustomerSummary {

	static final long serialVersionUID = 1L;

	public long id;
	public int termcode;
	public long date;
	public int windowDays = 365;
	public int recency = 0;
	public long frequency = 0;
	public double monetary = 0;

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
