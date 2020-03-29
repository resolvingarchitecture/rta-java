package ra.rta.enrich;

/**
 *
 */
public interface Enricher {
    void enrich(Enrichable event) throws Exception;
}
