package ra.rta.transform;

import java.util.Map;

import static ra.rta.utilities.JSONUtil.MAPPER;

public class JSONTransformer extends BaseTransformer {

	@Override
	protected void select() throws Exception {
		event.payload = MAPPER.readValue(event.rawPayload, Map.class);
	}
}
