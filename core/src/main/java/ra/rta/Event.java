package ra.rta;

import ra.rta.utilities.RandomUtil;

import java.io.Serializable;
import java.util.Map;

/**
 *
 */
public class Event implements Serializable {

    static final long serialVersionUID = 1L;
    public long id = 0;
    public long sourceId = 0;
    public int commandId = 0;
    public byte[] rawPayload;
    public String payloadTransformerClass;
    public Map<String,Object> payload;

    public Event() {
        id = RandomUtil.nextRandomLong();
    }

    public Event(long id) {
        this.id = id;
    }
}
