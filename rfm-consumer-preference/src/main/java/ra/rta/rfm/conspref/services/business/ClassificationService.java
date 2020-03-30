package ra.rta.rfm.conspref.services.business;

import java.util.*;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ra.rta.classify.KPI;
import ra.rta.rfm.conspref.services.data.DataServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.rfm.conspref.models.Classifiable;
import ra.rta.rfm.conspref.models.Classifier;
import ra.rta.Event;

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
		List<Classifier> classifiers = ((Classifiable)event).getClassifiers();
		for(Classifier classifier : classifiers) {
			classifier.classify((Classifiable) event, exactMatchTermcodeCache);
		}
	}

}
