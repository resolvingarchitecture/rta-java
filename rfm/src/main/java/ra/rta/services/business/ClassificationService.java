package ra.rta.services.business;

import java.util.*;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ra.rta.models.RFMKPI;
import ra.rta.publish.cassandra.DataServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.classify.Classifiable;
import ra.rta.classify.Classifier;
import ra.rta.models.Event;

public class ClassificationService {

	private static final Logger LOG = LoggerFactory.getLogger(ra.rta.classify.ClassificationService.class);

	private LoadingCache<String, LinkedHashSet<RFMKPI>> exactMatchTermcodeCache;

	public ClassificationService() {
		exactMatchTermcodeCache = CacheBuilder.newBuilder()
				.maximumSize(10000000) // 10M
				.build(
						new CacheLoader<String, LinkedHashSet<RFMKPI>>() {
							@Override
							public LinkedHashSet<RFMKPI> load(String description) throws Exception {
								return DataServiceManager.getWandDataService().lookupTermcode(description);
							}
						}
				);
	}

	public void classify(Event event) throws Exception {
		List<Classifier> classifiers = ((Classifiable)event).getClassifiers();
		for(Classifier classifier : classifiers) {
			classifier.classify((Classifiable) event, exactMatchTermcodeCache);
		}
	}

}
