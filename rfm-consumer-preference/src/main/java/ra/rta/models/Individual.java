package ra.rta.models;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public final class Individual {

	static final long serialVersionUID = 1L;

	public enum Status {
        Unknown, Active, Inactive, Closed, Deceased, Other, Revoked
	}

	public enum Type {
        Unknown, Consumer, PrivateMarket, SmallBusiness, BusinessBanking, seg, Corporation, Institution, Government
	}

	public Group group;
	public Set<KPI> kpis = new HashSet<>();
	public List<IndividualInteraction> individualInteractions = new ArrayList<>();

	public long id;
	public long gId;
	public Date openDate;
	public Integer openDateMonths;
	public Date closeDate;
	public Date processDate;
	public String bankBranch;
	public Status status;
	public Integer country;
	public String zipCode;
	public Integer age = 0;
	public Integer birthYear = 0;
	public Integer deceasedYear = 0;
	public Type type;
	public String subType1;
	public String subType2;
	public String subType3;
	public String state;
	public String languagePreference;
	public Double investableAssets;
	public Boolean estatementIndicator;
	public Boolean glbaOptOut;
	public Boolean solicitationCode;
	public Integer segmentation;
	public Date businessInceptionDate;
	public Integer businessSicCode;
	public Integer businessNaicsCode;
	public Double businessAnnualSales;
	public Integer businessNumEmployees;

	public boolean save = false;

	public void addKPI(KPI KPI) {
		kpis.add(KPI);
	}

	public void addKPI(int termcode) {
		KPI kpi = new KPI(termcode);
		kpi.date = new Date();
		kpis.add(kpi);
	}

	public void addKPI(int termcode, String date) throws Exception {
		KPI kpi = new KPI(termcode);
		kpi.date = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		kpis.add(kpi);
	}

    public void addKPI(int termcode, Date date) throws Exception {
        KPI kpi = new KPI(termcode);
		kpi.date = date;
        kpis.add(kpi);
    }

	public void addKPI(int termcode, int windowDays) {
		KPI kpi = new KPI(termcode);
		kpi.date = new Date();
		kpi.windowDays = windowDays;
		kpis.add(kpi);
	}

	public void addKPI(int termcode, String date, int windowDays) throws Exception {
		KPI kpi = new KPI(termcode);
		kpi.date = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		kpi.windowDays = windowDays;
		kpis.add(kpi);
	}

    public void addKPI(int termcode, Date date, int windowDays) throws Exception {
        KPI kpi = new KPI(termcode);
		kpi.date = date;
		kpi.windowDays = windowDays;
        kpis.add(kpi);
    }

	public void removeKPI(int termcode) {
		KPI KPI = new KPI(termcode);
		kpis.remove(KPI);
	}

	public void removeAllKPIs(Set<KPI> KPIsToRemove) {
		kpis.removeAll(KPIsToRemove);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Long.hashCode(gId);
		result = prime * result + Long.hashCode(id);
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
		Individual other = (Individual) obj;
		return Objects.equals(id, other.id) && Objects.equals(gId, other.gId);
	}
}
