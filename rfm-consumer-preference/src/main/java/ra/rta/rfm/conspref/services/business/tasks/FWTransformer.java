package ra.rta.rfm.conspref.services.business.tasks;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FWTransformer extends BaseTransformer {

	private static final Logger LOG = LoggerFactory.getLogger(FWTransformer.class);

	@Override
	protected void select() throws Exception {
		for (String fieldName : fieldMetaMap.keySet()) {
			Map<String, Object> fieldMetaPropertyMap = fieldMetaMap.get(fieldName);
			int startPosition = (Integer) fieldMetaPropertyMap.get("select_start_position") - 1;
			int endPosition = (Integer) fieldMetaPropertyMap.get("select_end_position") - 1;
            fieldNameValues.put(fieldName, raw.substring(startPosition, endPosition));
		}
//        LOG.info("Field Values Selected: "+ fieldNameValues);
	}

}
