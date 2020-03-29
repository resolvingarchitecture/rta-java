package ra.rta.models;

public interface Transformer {
    Event transform(Event event) throws Exception;
}
