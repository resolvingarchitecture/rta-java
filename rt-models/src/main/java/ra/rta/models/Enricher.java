package ra.rta.models;

/**
 *
 */
public interface Enricher {
    void enrich(EnrichableEvent event) throws Exception;
}
