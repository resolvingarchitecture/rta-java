package ra.rta.models;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.UUID;

public class KPICustomerSummary extends Entity {

	private static final long serialVersionUID = 1L;

	private UUID adId;
	private int termcode;
	private Integer date;
	private Integer windowDays = 365;
	private int recency = 0;
	private long frequency = 0;
	private double monetary = 0;

	public UUID getAdId() {
		return adId;
	}

	public void setAdId(UUID adId) {
		this.adId = adId;
	}

	public int getTermcode() {
		return termcode;
	}

	public void setTermcode(int termcode) {
		this.termcode = termcode;
	}

	public Integer getDate() {
		return date;
	}

	public void setDate(Integer date) {
		this.date = date;
	}

	public Integer getWindowDays() {
		return windowDays;
	}

	public void setWindowDays(Integer windowDays) {
		this.windowDays = windowDays;
	}

	public int getRecency() {
		return recency;
	}

	public void setRecency(int recency) {
		this.recency = recency;
	}

	public long getFrequency() {
		return frequency;
	}

	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}

	public double getMonetary() {
		return monetary;
	}

	public void setMonetary(double monetary) {
		this.monetary = monetary;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
