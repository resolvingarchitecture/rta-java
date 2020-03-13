package ra.rta.models;


import java.util.List;

/**
 *
 */
public interface ClassifiableEvent extends Event {
    List<Classifier> getClassifiers();
}
