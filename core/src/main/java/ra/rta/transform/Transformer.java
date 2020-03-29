package ra.rta.transform;

import ra.rta.models.Event;

public interface Transformer {
    void transform(Event event) throws Exception;
}
