package ra.rta.models;


import java.util.List;

/**
 * Identify others that are associated with Enrichers
 * and providing a list of those Enrichers.
 */
public interface Enrichable {
    List<Enricher> getEnrichers();
}
