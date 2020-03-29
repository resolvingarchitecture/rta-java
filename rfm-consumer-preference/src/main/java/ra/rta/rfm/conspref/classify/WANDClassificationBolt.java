package ra.rta.rfm.conspref.classify;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.BaseEventEmitterBolt;
import ra.rta.models.Classifiable;
import ra.rta.models.Classifier;
import ra.rta.models.Event;
import ra.rta.models.KPI;
import ra.rta.persistence.PersistenceManager;

/**
 * Add associated KPIs.
 */
public class WANDClassificationBolt extends BaseEventEmitterBolt {

	private static final long serialVersionUID = 1L;

	private Logger LOG = LoggerFactory.getLogger(WANDClassificationBolt.class);

	private LoadingCache<String, LinkedHashSet<KPI>> exactMatchTermcodeCache;

	@SuppressWarnings("hiding")
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		super.prepare(map, topologyContext, outputCollector);
		exactMatchTermcodeCache = CacheBuilder.newBuilder()
				.maximumSize(10000000) // 10M
				.build(new CacheLoader<String, LinkedHashSet<KPI>>() {
							@Override
							public LinkedHashSet<KPI> load(String description) throws Exception {
								return PersistenceManager.getWandDataService().lookupTermcode(description);
							}
						}
				);
	}

	public void execute(Event event) throws Exception {
		if(event instanceof Classifiable) {
			List<Classifier> classifiers = ((Classifiable) event).getClassifiers();
			for (Classifier classifier : classifiers) {
				classifier.classify((Classifiable) event, exactMatchTermcodeCache);
			}
		}
	}

}
