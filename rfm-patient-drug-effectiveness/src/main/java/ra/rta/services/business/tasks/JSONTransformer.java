package ra.rta.services.business.tasks;

import java.io.ByteArrayInputStream;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONTransformer extends BaseTransformer {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Override
	protected void select() throws Exception {
		// TODO Why create a new ByteArrayInputStream? readValue can take a
		// byte[]
		Map jsonMap = MAPPER.readValue(new ByteArrayInputStream(raw.getBytes()), Map.class);
		for(String fieldName : fieldMetaMap.keySet()) {
			Map<String,Object> fieldMetaPropertyMap = fieldMetaMap.get(fieldName);
			fieldNameValues.put(fieldName, jsonMap.get(fieldName));
		}
	}
}
