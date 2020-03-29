package ra.rta.models;

import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import ra.rta.utilities.JSONUtil;

public class Identity {

	static final long serialVersionUID = 1L;

	public enum Status {
        Unknown, Alive, Deceased
	}

	public Cluster cluster;
	public Set<KPI> kpis = new HashSet<>();
	public List<IdentityInteraction> identityInteractions = new ArrayList<>();

	// Identity Id
	public String id;
	// Cluster Id
	public String cId;
	public Status status;

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
		try {
			return JSONUtil.MAPPER.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return ReflectionToStringBuilder.toString(this);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Long.hashCode(cId);
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
		Identity other = (Identity) obj;
		return Objects.equals(id, other.id) && Objects.equals(cId, other.cId);
	}
}
