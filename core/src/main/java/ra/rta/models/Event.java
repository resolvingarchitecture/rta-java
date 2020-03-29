package ra.rta.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import ra.rta.utilities.JSONUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Event implements Serializable {

    static final long serialVersionUID = 1L;

    public long id = 0;

    public long indId = 0;
    public Identity identity;

    public int command = 0;
    public Map<String,Object> payload = new HashMap<>();
    public boolean save = false;

    @Override
    public String toString() {
        try {
            return JSONUtil.MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
