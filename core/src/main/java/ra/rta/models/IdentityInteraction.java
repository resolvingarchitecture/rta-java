package ra.rta.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import ra.rta.utilities.JSONUtil;

import java.util.Date;

public class IdentityInteraction {

	@SuppressWarnings("hiding")
	static final long serialVersionUID = 1L;

    public Identity identity;
    public String reltype;
    public String type;
    public String channel;
    public Date dateTime;
    public String memo;

    @Override
    public String toString() {
        try {
            return JSONUtil.MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
