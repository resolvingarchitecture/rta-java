package ra.rta.classify;

import java.util.*;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ra.rta.models.KPI;
import ra.rta.publish.cassandra.DataServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.models.Event;

public class ClassificationService {

	private static final Logger LOG = LoggerFactory.getLogger(ClassificationService.class);



	public ClassificationService() {

	}

	public void classify(Event event) throws Exception {
		List<Classifier> classifiers = ((Classifiable)event).getClassifiers();
		for(Classifier classifier : classifiers) {
			classifier.classify((Classifiable) event, exactMatchTermcodeCache);
		}
	}

}
