package ra.rta.transform;

import ra.rta.models.Event;

import static ra.rta.utilities.JSONUtil.MAPPER;

public class JSONTransformer extends BaseTransformer {

	@Override
	protected void select() throws Exception {
		event = MAPPER.readValue(raw, Event.class);
	}
}
