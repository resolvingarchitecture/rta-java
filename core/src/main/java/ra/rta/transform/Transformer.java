package ra.rta.transform;

import ra.rta.models.Event;

public interface Transformer {
    Event transform(byte[] data) throws Exception;
}
