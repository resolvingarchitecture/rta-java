package ra.rta.bolts;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.models.ClassifiableEvent;
import ra.rta.models.Event;
import ra.rta.services.business.ClassificationService;

/**
 * Add associated KPIs.
 */
public class ClassificationBolt extends BaseEventEmitterBolt {

	private static final long serialVersionUID = 1L;

	private Logger LOG = LoggerFactory.getLogger(ClassificationBolt.class);

	private ClassificationService classificationService;

	@SuppressWarnings("hiding")
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		super.prepare(map, topologyContext, outputCollector);
		classificationService = new ClassificationService();
	}

	public void execute(Event event) throws Exception {
		if (event instanceof ClassifiableEvent) {
			classificationService.classify(event);
		}
	}

}
