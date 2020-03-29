package ra.rta.transform;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONTransformer extends BaseTransformer {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Override
	protected void select() throws Exception {
		Map jsonMap = MAPPER.readValue(raw.getBytes(), Map.class);
		for(String fieldName : fieldMetaMap.keySet()) {
			Map<String,Object> fieldMetaPropertyMap = fieldMetaMap.get(fieldName);
			fieldNameValues.put(fieldName, fieldMetaPropertyMap.get(fieldName));
		}
	}
}
