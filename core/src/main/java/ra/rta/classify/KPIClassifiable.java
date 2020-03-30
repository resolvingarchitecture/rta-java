package ra.rta.classify;


import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface KPIClassifiable {
    List<KPI> classify(Map<String, LinkedHashSet<? extends KPI>> exactMatchTermcodeCache) throws Exception;
}
