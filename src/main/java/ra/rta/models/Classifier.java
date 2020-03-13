package ra.rta.models;

import com.google.common.cache.LoadingCache;

import java.util.LinkedHashSet;

/**
 *
 */
public interface Classifier {
    void classify(ClassifiableEvent event, LoadingCache<String, LinkedHashSet<KPI>> exactMatchTermcodeCache) throws Exception;
}
