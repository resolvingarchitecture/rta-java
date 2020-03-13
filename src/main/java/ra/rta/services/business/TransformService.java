package ra.rta.services.business;

import java.util.Map;

import ra.rta.models.Envelope;
import ra.rta.models.Event;
import ra.rta.models.Partner;
import ra.rta.models.Transformer;
import ra.rta.services.business.events.EventFactory;
import ra.rta.services.business.tasks.TransformerFactory;

/**
 * Transforms Event payloads to our internal object model graph.
 */
public class TransformService {

	private TransformerFactory transformerFactory;
	private EventFactory eventFactory;

	public TransformService(Map<String, Partner> allActivePartners, Map<String,Map<String, Map<String,Map<String,Object>>>> transformMap) throws Exception {
		transformerFactory = new TransformerFactory(allActivePartners, transformMap);
		eventFactory = new EventFactory(allActivePartners);
	}

	public Event transform(Envelope envelope) throws Exception {
		String partnerName = envelope.getBody().getRecords().get(0).getPartner().getName();
		Event event = eventFactory.build(partnerName, envelope);
        if(!envelope.getBody().getRecords().get(0).getTransformed()) {
            Transformer transformer = transformerFactory.build(partnerName, event);
            transformer.transform(event);
        }
		return event;
	}

}
