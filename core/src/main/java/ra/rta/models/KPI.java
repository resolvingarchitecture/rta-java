package ra.rta.models;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import ra.rta.utilities.JSONUtil;

/**
 * Key Performance Indicator
 */
public class KPI implements Cloneable {

	static final long serialVersionUID = 1L;

    public enum Type {Unknown, Personal, Group}

	public KPIIndividualSummary individualSummary;
	public KPIGroupSummary groupSummary;

	public long id;
	public int termcode;
	public Date date;
	public Type type = Type.Unknown;
	public Integer windowDays;
	public long tumblingWindowStart;
	public long tumblingWindowEnd;
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
		try {
			return JSONUtil.MAPPER.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return ReflectionToStringBuilder.toString(this);
		}
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
		kpi.individualSummary = individualSummary;
		kpi.groupSummary = groupSummary;
		kpi.type = type;
		kpi.windowDays = windowDays;
		kpi.save = save;
		return kpi;
	}
}
