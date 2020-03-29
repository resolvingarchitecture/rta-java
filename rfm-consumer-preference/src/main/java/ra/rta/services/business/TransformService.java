package ra.rta.services.business;

import java.util.Map;

import ra.rta.models.Event;
import ra.rta.models.Group;
import ra.rta.models.Transformer;
import ra.rta.services.business.tasks.TransformerFactory;

/**
 * Transforms Event payloads to our internal object model graph.
 */
public class TransformService {

	private TransformerFactory transformerFactory;

	public TransformService(Map<String, Group> allActivePartners, Map<String,Map<String, Map<String,Map<String,Object>>>> transformMap) throws Exception {
		transformerFactory = new TransformerFactory(allActivePartners, transformMap);
	}

	public Event transform(Event eventIn) throws Exception {
		Transformer transformer = transformerFactory.build(eventIn);
		return transformer.transform(eventIn);
	}

}
