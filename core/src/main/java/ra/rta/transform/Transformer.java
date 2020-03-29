package ra.rta.transform;

import ra.rta.models.Event;

public interface Transformer {
    Event transform(Event event) throws Exception;
}
