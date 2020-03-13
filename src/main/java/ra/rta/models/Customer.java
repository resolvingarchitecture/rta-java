package ra.rta.models;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public final class Customer extends Entity {

	private static final long serialVersionUID = 1L;

	public enum Status {
        Unknown, Active, Inactive, Closed, Deceased, Other, Revoked
	}

	public enum Type {
        Unknown, Consumer, PrivateMarket, SmallBusiness, BusinessBanking, seg, Corporation, Institution, Government
	}

	public enum Ethnicity {
		Unspecified, AmericanIndian, Asian, AfricanAmerican, Hispanic, Caucasian, PacificIslander
	}

	public enum Gender {
		Unknown, Male, Female
	}

	public enum MaritalStatus {
        Unknown, Married, Single, Divorced, Widowed, Other
	}

	private Partner partner;
	private Set<KPI> KPIS = new HashSet<>();
	private List<Account> accounts = new ArrayList<>();
	private List<CustomerInteraction> customerInteractions = new ArrayList<>();
	private List<Application> applications = new ArrayList<>();

	private UUID ucId;
	private UUID adId;
	private String uhId;
	private Date openDate;
	private Integer openDateMonths;
	private Date closeDate;
	private Date processDate;
	private String bankBranch;
	private Status status;
	private Integer country;
	private String zipCode;
	private Integer age = 0;
	private Integer birthYear = 0;
	private Integer deceasedYear = 0;
	private Type type;
	private String subType1;
	private String subType2;
	private String subType3;
	private String state;
	private Ethnicity ethnicity;
	private Gender gender;
	private String languagePreference;
	private MaritalStatus maritalStatus;
	private Double investableAssets;
	private Boolean estatementIndicator;
	private Boolean glbaOptOut;
	private Boolean solicitationCode;
	private Integer segmentation;
	private Date businessInceptionDate;
	private Integer businessSicCode;
	private Integer businessNaicsCode;
	private Double businessAnnualSales;
	private Integer businessNumEmployees;

	public void addKPI(KPI KPI) {
		KPIS.add(KPI);
	}

	public void addKPI(int termcode) {
		KPI KPI = new KPI(termcode);
		KPI.setDate(new Date());
		KPIS.add(KPI);
	}

	public void addKPI(int termcode, String date) throws Exception {
		KPI KPI = new KPI(termcode);
		KPI.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(date));
		KPIS.add(KPI);
	}

    public void addKPI(int termcode, Date date) throws Exception {
        KPI KPI = new KPI(termcode);
        KPI.setDate(date);
        KPIS.add(KPI);
    }

	public void addKPI(int termcode, int windowDays) {
		KPI KPI = new KPI(termcode);
		KPI.setDate(new Date());
		KPI.setWindowDays(windowDays);
		KPIS.add(KPI);
	}

	public void addKPI(int termcode, String date, int windowDays) throws Exception {
		KPI KPI = new KPI(termcode);
		KPI.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(date));
		KPI.setWindowDays(windowDays);
		KPIS.add(KPI);
	}

    public void addKPI(int termcode, Date date, int windowDays) throws Exception {
        KPI KPI = new KPI(termcode);
        KPI.setDate(date);
        KPI.setWindowDays(windowDays);
        KPIS.add(KPI);
    }

	public void removeKPI(int termcode) {
		KPI KPI = new KPI(termcode);
		KPIS.remove(KPI);
	}

	public void removeAllKPIs(Set<KPI> KPIsToRemove) {
		KPIS.removeAll(KPIsToRemove);
	}

	@Override
	public Partner getPartner() {
		return partner;
	}

	@Override
	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	public Set<KPI> getKPIS() {
		return KPIS;
	}

	public void setKPIS(Set<KPI> KPIS) {
		this.KPIS = KPIS;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

    public List<CustomerInteraction> getCustomerInteractions() {
        return customerInteractions;
    }

    public void setCustomerInteractions(List<CustomerInteraction> customerInteractions) {
        this.customerInteractions = customerInteractions;
    }

    public List<Application> getApplications() {
		return applications;
	}

	public void setApplications(List<Application> applications) {
		this.applications = applications;
	}

	public UUID getUcId() {
		return ucId;
	}

	public void setUcId(UUID ucId) {
		this.ucId = ucId;
	}

	public UUID getAdId() {
		return adId;
	}

	public void setAdId(UUID adId) {
		this.adId = adId;
	}

	public String getUhId() {
		return uhId;
	}

	public void setUhId(String uhId) {
		this.uhId = uhId;
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

	public Date getProcessDate() {
		return processDate;
	}

	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Integer getCountry() {
		return country;
	}

	public void setCountry(Integer country) {
		this.country = country;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(Integer birthYear) {
		this.birthYear = birthYear;
	}

	public Integer getDeceasedYear() {
		return deceasedYear;
	}

	public void setDeceasedYear(Integer deceasedYear) {
		this.deceasedYear = deceasedYear;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getSubType1() {
		return subType1;
	}

	public void setSubType1(String subType1) {
		this.subType1 = subType1;
	}

	public String getSubType2() {
		return subType2;
	}

	public void setSubType2(String subType2) {
		this.subType2 = subType2;
	}

	public String getSubType3() {
		return subType3;
	}

	public void setSubType3(String subType3) {
		this.subType3 = subType3;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Ethnicity getEthnicity() {
		return ethnicity;
	}

	public void setEthnicity(Ethnicity ethnicity) {
		this.ethnicity = ethnicity;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getLanguagePreference() {
		return languagePreference;
	}

	public void setLanguagePreference(String languagePreference) {
		this.languagePreference = languagePreference;
	}

	public MaritalStatus getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(MaritalStatus maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public Double getInvestableAssets() {
		return investableAssets;
	}

	public void setInvestableAssets(Double investableAssets) {
		this.investableAssets = investableAssets;
	}

	public Boolean getEstatementIndicator() {
		return estatementIndicator;
	}

	public void setEstatementIndicator(Boolean estatementIndicator) {
		this.estatementIndicator = estatementIndicator;
	}

	public Boolean getGlbaOptOut() {
		return glbaOptOut;
	}

	public void setGlbaOptOut(Boolean glbaOptOut) {
		this.glbaOptOut = glbaOptOut;
	}

	public Boolean getSolicitationCode() {
		return solicitationCode;
	}

	public void setSolicitationCode(Boolean solicitationCode) {
		this.solicitationCode = solicitationCode;
	}

	public Integer getSegmentation() {
		return segmentation;
	}

	public void setSegmentation(Integer segmentation) {
		this.segmentation = segmentation;
	}

	public Date getBusinessInceptionDate() {
		return businessInceptionDate;
	}

	public void setBusinessInceptionDate(Date businessInceptionDate) {
		this.businessInceptionDate = businessInceptionDate;
	}

	public Integer getBusinessSicCode() {
		return businessSicCode;
	}

	public void setBusinessSicCode(Integer businessSicCode) {
		this.businessSicCode = businessSicCode;
	}

	public Integer getBusinessNaicsCode() {
		return businessNaicsCode;
	}

	public void setBusinessNaicsCode(Integer businessNaicsCode) {
		this.businessNaicsCode = businessNaicsCode;
	}

	public Double getBusinessAnnualSales() {
		return businessAnnualSales;
	}

	public void setBusinessAnnualSales(Double businessAnnualSales) {
		this.businessAnnualSales = businessAnnualSales;
	}

	public Integer getBusinessNumEmployees() {
		return businessNumEmployees;
	}

	public void setBusinessNumEmployees(Integer businessNumEmployees) {
		this.businessNumEmployees = businessNumEmployees;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (partner == null ? 0 : partner.hashCode());
		result = prime * result + (ucId == null ? 0 : ucId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Customer other = (Customer) obj;
		return Objects.equals(ucId, other.ucId) && Objects.equals(partner, other.partner);
	}
}
