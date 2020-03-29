package ra.rta.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import ra.rta.utilities.JSONUtil;

public class KPIIndividualSummary {

	static final long serialVersionUID = 1L;

	public long id;
	public int termcode;
	public long date;
	public int windowDays = 365;

	@Override
	public String toString() {
		try {
			return JSONUtil.MAPPER.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return ReflectionToStringBuilder.toString(this);
		}
	}
}
