package ra.rta.models;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class KPIPartnerSummary extends Entity {

	private static final long serialVersionUID = 1L;

	private int termcode;
	private int date;
	private int windowDays = 365;
	private int recencyEarliest = 0;
	private int recencyLatest = 0;
	private int recencyBucket2Floor = 0;
	private int recencyBucket3Floor = 0;
	private long frequencyLeast = 0;
	private long frequencyMost = 0;
	private long frequencyBucket2Floor = 0;
	private long frequencyBucket3Floor = 0;
	private double monetaryLeast = 0.00;
	private double monetaryMost = 0.00;
	private double monetaryBucket2Floor = 0.00;
	private double monetaryBucket3Floor = 0.00;

	public int getTermcode() {
		return termcode;
	}

	public void setTermcode(int termcode) {
		this.termcode = termcode;
	}

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public int getWindowDays() {
		return windowDays;
	}

	public void setWindowDays(int windowDays) {
		this.windowDays = windowDays;
	}

	public int getRecencyEarliest() {
		return recencyEarliest;
	}

	public void setRecencyEarliest(int recencyEarliest) {
		this.recencyEarliest = recencyEarliest;
	}

	public int getRecencyLatest() {
		return recencyLatest;
	}

	public void setRecencyLatest(int recencyLatest) {
		this.recencyLatest = recencyLatest;
	}

	public int getRecencyBucket2Floor() {
		return recencyBucket2Floor;
	}

	public void setRecencyBucket2Floor(int recencyBucket2Floor) {
		this.recencyBucket2Floor = recencyBucket2Floor;
	}

	public int getRecencyBucket3Floor() {
		return recencyBucket3Floor;
	}

	public void setRecencyBucket3Floor(int recencyBucket3Floor) {
		this.recencyBucket3Floor = recencyBucket3Floor;
	}

	public long getFrequencyLeast() {
		return frequencyLeast;
	}

	public void setFrequencyLeast(long frequencyLeast) {
		this.frequencyLeast = frequencyLeast;
	}

	public long getFrequencyMost() {
		return frequencyMost;
	}

	public void setFrequencyMost(long frequencyMost) {
		this.frequencyMost = frequencyMost;
	}

	public long getFrequencyBucket2Floor() {
		return frequencyBucket2Floor;
	}

	public void setFrequencyBucket2Floor(long frequencyBucket2Floor) {
		this.frequencyBucket2Floor = frequencyBucket2Floor;
	}

	public long getFrequencyBucket3Floor() {
		return frequencyBucket3Floor;
	}

	public void setFrequencyBucket3Floor(long frequencyBucket3Floor) {
		this.frequencyBucket3Floor = frequencyBucket3Floor;
	}

	public double getMonetaryLeast() {
		return monetaryLeast;
	}

	public void setMonetaryLeast(double monetaryLeast) {
		this.monetaryLeast = monetaryLeast;
	}

	public double getMonetaryMost() {
		return monetaryMost;
	}

	public void setMonetaryMost(double monetaryMost) {
		this.monetaryMost = monetaryMost;
	}

	public double getMonetaryBucket2Floor() {
		return monetaryBucket2Floor;
	}

	public void setMonetaryBucket2Floor(double monetaryBucket2Floor) {
		this.monetaryBucket2Floor = monetaryBucket2Floor;
	}

	public double getMonetaryBucket3Floor() {
		return monetaryBucket3Floor;
	}

	public void setMonetaryBucket3Floor(double monetaryBucket3Floor) {
		this.monetaryBucket3Floor = monetaryBucket3Floor;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
