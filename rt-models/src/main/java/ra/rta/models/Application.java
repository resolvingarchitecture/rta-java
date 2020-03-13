package ra.rta.models;

import java.util.Date;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 *
 */
public final class Application extends Entity {

	private static final long serialVersionUID = 1L;

	public static final String APPLICATION_FIXED_WIDTH_COMMAND_PATH_JSON = "application_fixed_width";

	public static String APPLICATIONS_JSON = "applications";
	public static String APPLICATION_JSON = "application";
	public static String UCID_JSON = "ucId";
	public static String RAWDATA_JSON = "raw_data";

	private String aId;
	private String hId;
	private String reltype;
	private Date date;
	private Date decisionDate;
	private String branch;
	private String status;
	private String channel;
	private String zipCode;
	private String marketingCode;
	private Integer customScore;
	private Integer ficoScore;
	private Integer miscScore;
	private Double totalDebt;
	private Double totalIncome;
	private String collateralType;
	private Double collateralValue;
	private Double ltv;
	private Double cltv;
	private String lienPosition;
	private String overrideCode;
	private String declineCode1;
	private String declineCode2;
	private String declineCode3;
	private String type;
	private String prodType;
	private String subProdType;
	private String signerCode;
	private String segmentation;
	private Date businessInceptionDate;
	private String businessSICCode;
	private String businessNAICSCode;
	private Double businessAnnualSales;
	private Integer businessNumberEmployees;

	// private Date received;

	public String getaId() {
		return aId;
	}

	public void setaId(String aId) {
		this.aId = aId;
	}

	public String gethId() {
		return hId;
	}

	public void sethId(String hId) {
		this.hId = hId;
	}

	public String getReltype() {
		return reltype;
	}

	public void setReltype(String reltype) {
		this.reltype = reltype;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDecisionDate() {
		return decisionDate;
	}

	public void setDecisionDate(Date decisionDate) {
		this.decisionDate = decisionDate;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getMarketingCode() {
		return marketingCode;
	}

	public void setMarketingCode(String marketingCode) {
		this.marketingCode = marketingCode;
	}

	public Integer getCustomScore() {
		return customScore;
	}

	public void setCustomScore(Integer customScore) {
		this.customScore = customScore;
	}

	public Integer getFicoScore() {
		return ficoScore;
	}

	public void setFicoScore(Integer ficoScore) {
		this.ficoScore = ficoScore;
	}

	public Integer getMiscScore() {
		return miscScore;
	}

	public void setMiscScore(Integer miscScore) {
		this.miscScore = miscScore;
	}

	public Double getTotalDebt() {
		return totalDebt;
	}

	public void setTotalDebt(Double totalDebt) {
		this.totalDebt = totalDebt;
	}

	public Double getTotalIncome() {
		return totalIncome;
	}

	public void setTotalIncome(Double totalIncome) {
		this.totalIncome = totalIncome;
	}

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public Double getCollateralValue() {
		return collateralValue;
	}

	public void setCollateralValue(Double collateralValue) {
		this.collateralValue = collateralValue;
	}

	public Double getLtv() {
		return ltv;
	}

	public void setLtv(Double ltv) {
		this.ltv = ltv;
	}

	public Double getCltv() {
		return cltv;
	}

	public void setCltv(Double cltv) {
		this.cltv = cltv;
	}

	public String getLienPosition() {
		return lienPosition;
	}

	public void setLienPosition(String lienPosition) {
		this.lienPosition = lienPosition;
	}

	public String getOverrideCode() {
		return overrideCode;
	}

	public void setOverrideCode(String overrideCode) {
		this.overrideCode = overrideCode;
	}

	public String getDeclineCode1() {
		return declineCode1;
	}

	public void setDeclineCode1(String declineCode1) {
		this.declineCode1 = declineCode1;
	}

	public String getDeclineCode2() {
		return declineCode2;
	}

	public void setDeclineCode2(String declineCode2) {
		this.declineCode2 = declineCode2;
	}

	public String getDeclineCode3() {
		return declineCode3;
	}

	public void setDeclineCode3(String declineCode3) {
		this.declineCode3 = declineCode3;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProdType() {
		return prodType;
	}

	public void setProdType(String prodType) {
		this.prodType = prodType;
	}

	public String getSubProdType() {
		return subProdType;
	}

	public void setSubProdType(String subProdType) {
		this.subProdType = subProdType;
	}

	public String getSignerCode() {
		return signerCode;
	}

	public void setSignerCode(String signerCode) {
		this.signerCode = signerCode;
	}

	public String getSegmentation() {
		return segmentation;
	}

	public void setSegmentation(String segmentation) {
		this.segmentation = segmentation;
	}

	public Date getBusinessInceptionDate() {
		return businessInceptionDate;
	}

	public void setBusinessInceptionDate(Date businessInceptionDate) {
		this.businessInceptionDate = businessInceptionDate;
	}

	public String getBusinessSICCode() {
		return businessSICCode;
	}

	public void setBusinessSICCode(String businessSICCode) {
		this.businessSICCode = businessSICCode;
	}

	public String getBusinessNAICSCode() {
		return businessNAICSCode;
	}

	public void setBusinessNAICSCode(String businessNAICSCode) {
		this.businessNAICSCode = businessNAICSCode;
	}

	public Double getBusinessAnnualSales() {
		return businessAnnualSales;
	}

	public void setBusinessAnnualSales(Double businessAnnualSales) {
		this.businessAnnualSales = businessAnnualSales;
	}

	public Integer getBusinessNumberEmployees() {
		return businessNumberEmployees;
	}

	public void setBusinessNumberEmployees(Integer businessNumberEmployees) {
		this.businessNumberEmployees = businessNumberEmployees;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
