package ra.rta.classify;

import com.google.common.cache.LoadingCache;
import ra.rta.models.KPI;

import java.util.LinkedHashSet;

/**
 *
 */
public interface Classifier {
    void classify(Classifiable event, LoadingCache<String, LinkedHashSet<KPI>> exactMatchTermcodeCache) throws Exception;
}
