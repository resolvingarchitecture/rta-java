package ra.rta.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import ra.rta.utilities.JSONUtil;

@Table
public final class Cluster {

	private static final long serialVersionUID = 1L;

	@PrimaryKey
	public String name;
	public String core;
	@Column("command_maps")
	public Map<String,String> commandMaps = new HashMap<>();
	public Map<String,String> formatters = new HashMap<>();
	public Boolean unified;
	public String version;
	public Boolean active;

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
		Cluster other = (Cluster) obj;
		return Objects.equals(name, other.name);
	}
}
