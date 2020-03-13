package ra.rta.models;

import java.util.Date;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 *
 */
public class CardAccount extends Account {

	@SuppressWarnings("hiding")
	static final long serialVersionUID = 1L;

	public enum SubType {
		Control(1), Individual(2), Sub(3);
		private int id;
		SubType(int id) {this.id = id;}
		public int getId() {return id;}
	}

	public enum Affiliation {
		Visa(1), Mastercard(2), AmericanExpress(3), Discover(4), Other(5);
		private int id;
		Affiliation(int id) {this.id = id;}
		public int getId() {return id;}
	}

	public enum Status {
		Active(1), Inactive(2), Stolen(3), Fraud(4), ChargedOff(5), Lost(6), Bankrupt(7), Frozen(8), Closed(9),
		Revoked(10), NonAccrual(11), Overlimit(12);
		private int id;
		Status(int id) {this.id = id;}
		public int getId() {return id;}
	}

	private SubType subType;
	private Affiliation affiliation;
	private Status status;
	private String subtypeCode1;
	private String subtypeCode2;
	private String subtypeCode3;
	private String pricingStrategyCode1;
	private String pricingStrategyCode2;
	private String miscUser1;
	private String miscUser2;
	private String miscUser3;
	private Boolean estatementIndicator;
	private Date openDate;
	private Integer openDateMonths;
	private Double costOfFundsRate;
	private Date lastPaymentDate;
	private Double lastPaymentAmount;
	private Date nextDueDate;
	private Double nextDueAmount;
	private Double avgDailyBalance;
	private Double balance;
	private Double merchPurch;
	private Double cash;
	private Double promo;
	private Double totalCreditLine;
	private Date creditLineDate;
	private Double newCharges;
	private Double pendingCharges;
	private Double lastStatementBalance;
	private Double lastStatementPayment;
	private Double lastStatementInterest;
	private Double lastStatementLateFee;
	private Double lastStatementOvrFee;
	private Double lastStatementForeignFee;
	private Double lastStatementMiscFees;
	private Double lastStatementTransactionFee;
	private Double lastStatementMerchPurch;
	private Double lastStatementCashAmount;
	private Double lastStatementCheckAmount;
	private Double lastStatementPromoAmount;
	private Integer lastStatementCashNum;
	private Integer lastStatementMerchNum;
	private Integer lastStatementCheckNum;
	private Double totalCashLimit;
	private Double currentDPD;
	private Double currentCPD;
	private String statusCode1;
	private Date statusCode1Date;
	private String statusCode2;
	private Date statusCode2Date;
	private String statusCode3;
	private Date statusCode3Date;
	private Double highBalance;
	private Date highBalanceDate;
	private Date lastOverLimitDate;
	private Double lastCashAmount;
	private Date lastCashDate;
	private Date lastPurchDate;
	private Double lastPurchAmount;
	private Boolean authUserFlag;
	private Double paymentHistory12;
	private Double paymentHistory24;
	private Double availableCredit;
	private Double availableCash;
	private Double cashAPR;
	private Double merchAPR;


	private Date closeDate;
	private Double availableBalance;
	private String productType;
	private String accountType;
	private Date statusDate;

	private Date amortizationDate;
	private Double remainingPayment;
	private Integer term;
	private Date balanceDate;
	private Date maturityDate;
	private Boolean overdraftProtection;
	private Date renewalDate;
	private Integer renewalTimes;
	private Double interestRate;
	private Double interestEarnedYTD;
	private Double prevYearInterest;
	private Double feesPaidYTD;
	private Double feesPaidMTD;
	private Double feesWaivedYTD;
	private Double feesWaivedMTD;
	private Date lastOverdraftDate;
	private Integer numOverdrawnYTD;
	private Integer numOverdrawnMTD;
	private Double principalBalance;
	private Double premiumAmount;
	private Double remainingBalance;

	private Double apr;

	private String collateralType;
	private Double collateralValue;
	private Double minimalPayment;
	private Double endingBalance;


	private Integer daysPastDue;
	private Integer pastDueYTD;
	private Integer pastDueLTD;
	private Boolean inCollections;

	// Auto
	private String vehicleNumber;
	private String vehicleMake;
	private String vehicleModel;
	private String vehicleYear;
	private Date vehicleDateAdded;
	private String vehicleLienholder;

	private Double cashValue;
	private Double deathBenefit;
	private Integer policyTerm;
	private Date policyTermFromDate;
	private Date policyTermToDate;
	private Double liabilityLimitSingle;
	private Double liabilityLimitTotal;
	private Date effectiveDate;
	private Date expirationDate;

	public SubType getSubType() {
		return subType;
	}

	public void setSubType(SubType subType) {
		this.subType = subType;
	}

	public Affiliation getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(Affiliation affiliation) {
		this.affiliation = affiliation;
	}

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
