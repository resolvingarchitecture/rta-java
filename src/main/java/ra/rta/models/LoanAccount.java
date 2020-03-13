package ra.rta.models;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class LoanAccount extends Account {

	@SuppressWarnings("hiding")
	static final long serialVersionUID = 1L;

	private String openDate;
	private Integer openDateMonths;
	private String closeDate;

	public String getOpenDate() {
		return openDate;
	}

	public void setOpenDate(String openDate) {
		this.openDate = openDate;
	}

	public Integer getOpenDateMonths() {
		return openDateMonths;
	}

	public void setOpenDateMonths(Integer openDateMonths) {
		this.openDateMonths = openDateMonths;
	}

	public String getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(String closeDate) {
		this.closeDate = closeDate;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
