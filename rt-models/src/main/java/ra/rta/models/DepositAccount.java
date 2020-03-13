package ra.rta.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class DepositAccount extends Account {

	@SuppressWarnings("hiding")
	static final long serialVersionUID = 1L;

	public enum Status {
		Unknown, Open, Closed, ChargedOff, Bankrupt, Frozen, Fraud
	}

	private List<DepositTransaction> depositTransactions = new ArrayList<>();

	private Status status;
	private Date statusDate;
	private String type;
	private String subtypeCode1;
	private String subtypeCode2;
	private String subtypeCode3;
	private Boolean estatementIndicator;
	private Date openDate;
	private Integer openDateMonths;
	private Date closeDate;
	private Float balance;
	private Float availableBalance;
	private Date maturityDate;
	private Date renewalDate;
	private Integer renewalTimes;
	private Boolean overdraftProtection;
	private Float interestRate;
	private Float interestEarnedYTD;
	private Float prevYearInterest;
	private Float feesPaidYTD;
	private Float feesPaidMTD;
	private Float feesWaivedYTD;
	private Float feesWaivedMTD;
	private Date lastOverdraftDate;
	private Float numOverdrawnYTD;
	private Float numOverdrawnMTD;

	public List<DepositTransaction> getDepositTransactions() {
		return depositTransactions;
	}

	public void setDepositTransactions(List<DepositTransaction> depositTransactions) {
		this.depositTransactions = depositTransactions;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubtypeCode1() {
		return subtypeCode1;
	}

	public void setSubtypeCode1(String subtypeCode1) {
		this.subtypeCode1 = subtypeCode1;
	}

	public String getSubtypeCode2() {
		return subtypeCode2;
	}

	public void setSubtypeCode2(String subtypeCode2) {
		this.subtypeCode2 = subtypeCode2;
	}

	public String getSubtypeCode3() {
		return subtypeCode3;
	}

	public void setSubtypeCode3(String subtypeCode3) {
		this.subtypeCode3 = subtypeCode3;
	}

	public Boolean getEstatementIndicator() {
		return estatementIndicator;
	}

	public void setEstatementIndicator(Boolean estatementIndicator) {
		this.estatementIndicator = estatementIndicator;
	}

	public Date getOpenDate() {
		return openDate;
	}

	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}

	public Integer getOpenDateMonths() {
		return openDateMonths;
	}

	public void setOpenDateMonths(Integer openDateMonths) {
		this.openDateMonths = openDateMonths;
	}

	public Date getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Float getBalance() {
		return balance;
	}

	public void setBalance(Float balance) {
		this.balance = balance;
	}

	public Float getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(Float availableBalance) {
		this.availableBalance = availableBalance;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public Date getRenewalDate() {
		return renewalDate;
	}

	public void setRenewalDate(Date renewalDate) {
		this.renewalDate = renewalDate;
	}

	public Integer getRenewalTimes() {
		return renewalTimes;
	}

	public void setRenewalTimes(Integer renewalTimes) {
		this.renewalTimes = renewalTimes;
	}

	public Boolean getOverdraftProtection() {
		return overdraftProtection;
	}

	public void setOverdraftProtection(Boolean overdraftProtection) {
		this.overdraftProtection = overdraftProtection;
	}

	public Float getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Float interestRate) {
		this.interestRate = interestRate;
	}

	public Float getInterestEarnedYTD() {
		return interestEarnedYTD;
	}

	public void setInterestEarnedYTD(Float interestEarnedYTD) {
		this.interestEarnedYTD = interestEarnedYTD;
	}

	public Float getPrevYearInterest() {
		return prevYearInterest;
	}

	public void setPrevYearInterest(Float prevYearInterest) {
		this.prevYearInterest = prevYearInterest;
	}

	public Float getFeesPaidYTD() {
		return feesPaidYTD;
	}

	public void setFeesPaidYTD(Float feesPaidYTD) {
		this.feesPaidYTD = feesPaidYTD;
	}

	public Float getFeesPaidMTD() {
		return feesPaidMTD;
	}

	public void setFeesPaidMTD(Float feesPaidMTD) {
		this.feesPaidMTD = feesPaidMTD;
	}

	public Float getFeesWaivedYTD() {
		return feesWaivedYTD;
	}

	public void setFeesWaivedYTD(Float feesWaivedYTD) {
		this.feesWaivedYTD = feesWaivedYTD;
	}

	public Float getFeesWaivedMTD() {
		return feesWaivedMTD;
	}

	public void setFeesWaivedMTD(Float feesWaivedMTD) {
		this.feesWaivedMTD = feesWaivedMTD;
	}

	public Date getLastOverdraftDate() {
		return lastOverdraftDate;
	}

	public void setLastOverdraftDate(Date lastOverdraftDate) {
		this.lastOverdraftDate = lastOverdraftDate;
	}

	public Float getNumOverdrawnYTD() {
		return numOverdrawnYTD;
	}

	public void setNumOverdrawnYTD(Float numOverdrawnYTD) {
		this.numOverdrawnYTD = numOverdrawnYTD;
	}

	public Float getNumOverdrawnMTD() {
		return numOverdrawnMTD;
	}

	public void setNumOverdrawnMTD(Float numOverdrawnMTD) {
		this.numOverdrawnMTD = numOverdrawnMTD;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}
