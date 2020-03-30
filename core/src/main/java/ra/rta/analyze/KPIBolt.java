package ra.rta.analyze;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.LoggerFactory;
import ra.rta.BaseEventEmitterBolt;
import ra.rta.Event;

/**
 * Use Drools Rules Engine to determine KPIs.
 */
public class KPIBolt extends BaseEventEmitterBolt {

	private static final long serialVersionUID = 1L;

	private StatelessKieSession session;

	@Override
	@SuppressWarnings("hiding")
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		super.prepare(map, topologyContext, outputCollector);
		String kBaseName = (String) map.get("topology.rules.kBaseName");
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kContainer = kieServices.getKieClasspathContainer();
		KieBase kBase = kContainer.getKieBase(kBaseName);
		session = kBase.newStatelessKieSession();
		session.setGlobal("logger", LoggerFactory.getLogger(kBaseName));
	}

	@Override
	public void execute(Event event) {
		session.execute(event);
	}

}
