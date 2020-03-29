package ra.rta.analyze.rules;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.LoggerFactory;

public class StatelessKieSessionFactory {

	private final StatelessKieSession session;

	public StatelessKieSessionFactory(String kBaseName) {
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kContainer = kieServices.getKieClasspathContainer();
		KieBase kBase = kContainer.getKieBase(kBaseName);
		session = kBase.newStatelessKieSession();
		session.setGlobal("logger", LoggerFactory.getLogger(kBaseName));
	}

	public StatelessKieSession getStatelessKieSession() {
		return session;
	}
}
