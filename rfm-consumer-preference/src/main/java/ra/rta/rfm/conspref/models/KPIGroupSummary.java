package ra.rta.rfm.conspref.models;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class KPIGroupSummary {

	static final long serialVersionUID = 1L;

	public int termcode;
	public int date;
	public int windowDays = 365;
	public int recencyEarliest = 0;
	public int recencyLatest = 0;
	public int recencyBucket2Floor = 0;
	public int recencyBucket3Floor = 0;
	public long frequencyLeast = 0;
	public long frequencyMost = 0;
	public long frequencyBucket2Floor = 0;
	public long frequencyBucket3Floor = 0;
	public double monetaryLeast = 0.00;
	public double monetaryMost = 0.00;
	public double monetaryBucket2Floor = 0.00;
	public double monetaryBucket3Floor = 0.00;

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
