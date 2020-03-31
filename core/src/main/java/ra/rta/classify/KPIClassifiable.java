package ra.rta.classify;

import java.util.List;
import java.util.Set;

/**
 *
 */
public interface KPIClassifiable {
    List<KPI> classify(Set<String> descriptions) throws Exception;
}
