package ra.rta.models;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 *
 */
public class OtherAccount extends Account {

	@SuppressWarnings("hiding")
	static final long serialVersionUID = 1L;

	public enum Status {
		Active(1),
		Inactive(2),
		Stolen(3),
		Fraud(4),
		ChargedOff(5),
		Lost(6),
		Bankrupt(7),
		Frozen(8),
		Closed(9),
		Revoked(10),
		NonAccrual(11);
		private int id;
		Status(int id){this.id = id;}
		public int getId(){return id;}
	}

	private Status status;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
