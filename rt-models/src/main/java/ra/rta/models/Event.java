package ra.rta.models;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 */
public interface Event extends Serializable {
    UUID getId();
    // Provide complete Envelope for context
    Envelope getEnvelope();
    // Provide easy access to the concrete Entity for this Event
    Entity getEntity();
}
