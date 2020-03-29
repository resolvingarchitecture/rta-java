package ra.rta.rfm.conspref.classify;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.rfm.conspref.models.Classifiable;
import ra.rta.models.Event;
import ra.rta.rfm.conspref.services.business.ClassificationService;

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
		if (event instanceof Classifiable) {
			classificationService.classify(event);
		}
	}

}
