package ra.rta.models;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Key Performance Indicator
 */
public final class KPI implements Cloneable {

	static final long serialVersionUID = 1L;

    public enum Type {Unknown, Flag, Derived, Competitive, NonCompetitive}

	public KPIIndividualSummary individualSummary;
	public KPIGroupSummary groupSummary;

	public long id;
	public int termcode;
	public Date date;
	public Type type = Type.Unknown;
	public Integer recency;
	public Integer recencyScore = 0;
	public Integer frequency = 0;
	public Integer frequencyScore = 0;
	public Double monetary = 0.0;
	public Integer monetaryScore = 0;
	public Integer windowDays;
	public Date determinationDate;
	public String description;
	public Boolean active = false;
	public Boolean save = false;

	public KPI() { }

	public KPI(int termcode) {
		this.termcode = termcode;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	@Override
	public int hashCode() {
		return termcode;
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
		KPI other = (KPI) obj;
		return Objects.equals(termcode, other.termcode);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		super.clone();
		KPI kpi = new KPI(termcode);
		kpi.active = active;
		kpi.id = id;
		kpi.date = date;
		kpi.description = description;
		kpi.determinationDate = determinationDate;
		kpi.frequency = frequency;
		kpi.frequencyScore = frequencyScore;
		kpi.individualSummary = individualSummary;
		kpi.groupSummary = groupSummary;
		kpi.monetary = monetary;
		kpi.monetaryScore = monetaryScore;
		kpi.recency = recency;
		kpi.recencyScore = recencyScore;
		kpi.type = type;
		kpi.windowDays = windowDays;
		kpi.save = save;
		return kpi;
	}
}
