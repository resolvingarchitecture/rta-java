package ra.rta.models;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class DepositTransaction extends Transaction {

	@SuppressWarnings("hiding")
	static final long serialVersionUID = 1L;

	private Integer checkNumber;

	public Integer getCheckNumber() {
		return checkNumber;
	}

	public void setCheckNumber(Integer checkNumber) {
		this.checkNumber = checkNumber;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}
