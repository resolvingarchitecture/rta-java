package ra.rta.rfm.conspref.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public final class Group {

	private static final long serialVersionUID = 1L;

	public int id;
	public String name;
	public String core;
	public Map<String,String> commandMaps = new HashMap<>();
	public Map<String,String> formatters = new HashMap<>();
	public Boolean unified;
	public String version;
	public Boolean active;

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	@Override
	public int hashCode() {
		return name == null ? 0 : name.hashCode();
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
		Group other = (Group) obj;
		return Objects.equals(id, other.id);
	}
}
