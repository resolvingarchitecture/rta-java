package ra.rta.rfm.conspref.models;

import ra.rta.models.Event;

public interface Transformer {
    ra.rta.models.Event transform(Event event) throws Exception;
}
