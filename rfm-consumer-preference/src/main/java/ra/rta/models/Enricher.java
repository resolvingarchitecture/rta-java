package ra.rta.models;

/**
 *
 */
public interface Enricher {
    void enrich(Enrichable event) throws Exception;
}
