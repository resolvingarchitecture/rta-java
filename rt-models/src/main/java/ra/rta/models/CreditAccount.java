package ra.rta.models;

import java.util.Date;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CreditAccount extends Account {

	@SuppressWarnings("hiding")
	static final long serialVersionUID = 1L;

	public enum Status {
		Open(1),
		ChargedOff(3),
		Bankrupt(4),
		Fraud(6),
		PaidInFull(7),
		Foreclosed(8),
		NonAccrual(9);
		private int id;
		Status(int id){this.id = id;}
		public int getId() {return id;}
	}

	public enum CollateralType {
		Cash(1),
		RealEstate(2),
		Vehicle(3),
		None(4),
		WorkingCapital(5),
		Equipment(6);
		private int id;
		CollateralType(int id){this.id = id;}
		public int getId() {return id;}
	}

	private Status status;
	private CollateralType collateralType;

	private String type;
	private String subtype;
	private Date openDate;
	private Integer openDateMonths;
	private Date closeDate;
	private Double balance;
	private Double availableBalance;
	private String productType;
	private String accountType;
	private String subtypeCode1;
	private String subtypeCode2;
	private String subtypeCode3;
	private String pricingStrategyCode1;
	private String pricingStrategyCode2;
	private String miscUser1;
	private String miscUser2;
	private String miscUser3;
	private String affiliation;
	private Date statusDate;
	private String statusCode1;
	private Date statusCode1Date;
	private String statusCode2;
	private Date statusCode2Date;
	private String statusCode3;
	private Date statusCode3Date;
	private Date amortizationDate;
	private Double remainingPayment;
	private Integer term;
	private Double costOfFundsRate;
	private Date lastPaymentDate;
	private Double lastPaymentAmount;
	private Date nextDueDate;
	private Double nextDueAmount;
	private Double avgDailyBalance;
	private Date balanceDate;
	private Double merchPurch;
	private Double cash;
	private Double promo;
	private Boolean estatementIndicator;
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
	private Double lastStatementBalance;
	private Double lastStatementPayment;
	private Double lastStatementInterest;
	private Double lastStatementLateFee;
	private Double lastStatementOvrFee;
	private Double lastStatementForeignFee;
	private Double lastStatementMiscFees;
	private Double lastStatementTransactionFee;
	private Double lastStatementFees;
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
	private Double apr;
	private Double cashAPR;
	private Double merchAPR;
	private Double collateralValue;
	private Double minimalPayment;
	private Double endingBalance;
	private Double totalCreditLine;
	private Date creditLineDate;
	private Double availableCredit;
	private Double availableCash;
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
	private String save;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public CollateralType getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(CollateralType collateralType) {
		this.collateralType = collateralType;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
