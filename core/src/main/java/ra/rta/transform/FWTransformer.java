package ra.rta.transform;

import java.util.Map;

public class FWTransformer extends BaseTransformer {

	@Override
	protected void select() throws Exception {
		String body = new String(event.rawPayload);
		for (String fieldName : fieldMetaMap.keySet()) {
			Map<String, Object> fieldMetaPropertyMap = fieldMetaMap.get(fieldName);
			int startPosition = (Integer) fieldMetaPropertyMap.get("select_start_position") - 1;
			int endPosition = (Integer) fieldMetaPropertyMap.get("select_end_position") - 1;
            fieldNameValues.put(fieldName, body.substring(startPosition, endPosition));
		}
	}

}
