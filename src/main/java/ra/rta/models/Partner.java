package ra.rta.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
public final class Partner extends Entity {

	private static final long serialVersionUID = 1L;

	@PrimaryKey
	private String name;
	private String core;
	@Column("command_maps")
	private Map<String,String> commandMaps = new HashMap<>();
	private Map<String,String> formatters = new HashMap<>();
	private Boolean unified;
	private String version;
	private Boolean active;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCore() {
		return core;
	}

	public void setCore(String core) {
		this.core = core;
	}

	public Map<String, String> getCommandMaps() {
		return commandMaps;
	}

	public void setCommandMaps(Map<String, String> commandMaps) {
		this.commandMaps = commandMaps;
	}

	public Map<String, String> getFormatters() {
		return formatters;
	}

	public void setFormatters(Map<String, String> formatters) {
		this.formatters = formatters;
	}

	public Boolean getUnified() {
		return unified;
	}

	public void setUnified(Boolean unified) {
		this.unified = unified;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

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
		Partner other = (Partner) obj;
		return Objects.equals(name, other.name);
	}
}
