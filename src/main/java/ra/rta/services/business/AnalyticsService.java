package ra.rta.services.business;

import java.util.List;
import java.util.Map;

import ra.rta.rules.TransactionFactAssembler;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.rules.CustomerFactAssembler;
import ra.rta.rules.StatelessKieSessionFactory;
import ra.rta.models.Customer;
import ra.rta.models.Entity;
import ra.rta.models.Event;
import ra.rta.models.Transaction;

/**
 * Use embedded Drools Rules Engine to determine KPIs.
 */
public class AnalyticsService {

	private static final Logger LOG = LoggerFactory.getLogger(AnalyticsService.class);

	private StatelessKieSession session;

	public AnalyticsService(Map properties) {
		String kBaseName = (String) properties.get("topology.rules.kBaseName");
		StatelessKieSessionFactory factory = new StatelessKieSessionFactory(kBaseName);
		session = factory.getStatelessKieSession();
	}

	public void analyze(Event event) {
        List<Entity> facts = null;
		Entity entity = event.getEntity();
        if(entity instanceof Transaction)
            facts = TransactionFactAssembler.getFacts((Transaction)entity);
        else if(entity instanceof Customer)
            facts = CustomerFactAssembler.getFacts((Customer)entity);
        else
            return;
        // Fire all rules on record's facts
        session.execute(facts);
	}
}
