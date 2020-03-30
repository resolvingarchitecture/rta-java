package ra.rta.transform;

import ra.rta.Event;

public interface Transformer {
    void transform(Event event) throws Exception;
}
