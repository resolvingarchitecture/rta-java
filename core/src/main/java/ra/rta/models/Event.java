package ra.rta.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Event implements Serializable {
    static final long serialVersionUID = 1L;
    public long id = 0;
    public long groupId = 0;
    public int command = 0;
    public Map<String,Object> payload = new HashMap<>();
    public boolean save = false;
}
