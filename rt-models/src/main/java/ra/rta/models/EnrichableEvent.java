package ra.rta.models;


import java.util.List;

/**
 * Identify Events that are associated with Enrichers
 * and providing a list of those Enrichers.
 */
public interface EnrichableEvent extends Event {
    List<Enricher> getEnrichers();
}
