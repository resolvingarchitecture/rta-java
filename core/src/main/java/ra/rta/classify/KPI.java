package ra.rta.classify;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Key Performance Indicator
 */
public class KPI implements Cloneable {

	static final long serialVersionUID = 1L;

	public long id;
	public int termcode;
	public Date date;
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
	public KPI clone() throws CloneNotSupportedException {
		KPI kpi = (KPI)super.clone();
		kpi.date = (Date)date.clone();
		kpi.determinationDate = (Date)determinationDate.clone();
		return kpi;
	}
}
