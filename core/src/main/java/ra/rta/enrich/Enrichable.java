package ra.rta.enrich;

import java.util.Map;

/**
 * Identify others that are associated with Enrichers
 * and providing a list of those Enrichers.
 */
public interface Enrichable {
    void enrich(Map<String,Object> input) throws Exception;
}
