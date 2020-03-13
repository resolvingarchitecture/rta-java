package ra.rta.services.business;

import java.util.*;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ra.rta.models.KPI;
import ra.rta.services.data.DataServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.models.ClassifiableEvent;
import ra.rta.models.Classifier;
import ra.rta.models.Event;

public class ClassificationService {

	private static final Logger LOG = LoggerFactory.getLogger(ClassificationService.class);

	private LoadingCache<String, LinkedHashSet<KPI>> exactMatchTermcodeCache;

	public ClassificationService() {
		exactMatchTermcodeCache = CacheBuilder.newBuilder()
				.maximumSize(10000000) // 10M
				.build(
						new CacheLoader<String, LinkedHashSet<KPI>>() {
							@Override
							public LinkedHashSet<KPI> load(String description) throws Exception {
								return DataServiceManager.getWandDataService().lookupTermcode(description);
							}
						}
				);
	}

	public void classify(Event event) throws Exception {
		List<Classifier> classifiers = ((ClassifiableEvent)event).getClassifiers();
		for(Classifier classifier : classifiers) {
			classifier.classify((ClassifiableEvent) event, exactMatchTermcodeCache);
		}
	}

}
