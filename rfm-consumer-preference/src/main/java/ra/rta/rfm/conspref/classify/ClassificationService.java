package ra.rta.rfm.conspref.classify;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.models.Classifiable;
import ra.rta.models.Classifier;
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
