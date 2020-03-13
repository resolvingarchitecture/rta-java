package ra.rta.bolts;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import ra.rta.models.Event;
import ra.rta.services.business.AnalyticsService;

/**
 * Use Analytics Service to determine KPIs.
 */
public class AnalyticsBolt extends BaseEventEmitterBolt {

	private static final long serialVersionUID = 1L;

	private AnalyticsService analyticsService;

	@Override
	@SuppressWarnings("hiding")
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		super.prepare(map, topologyContext, outputCollector);
		analyticsService = new AnalyticsService(map);
	}

	@Override
	public void execute(Event event) throws Exception {
		analyticsService.analyze(event);
	}

}
