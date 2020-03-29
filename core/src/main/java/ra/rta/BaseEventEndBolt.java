package ra.rta;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import ra.rta.models.Event;

public abstract class BaseEventEndBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(BaseEventEndBolt.class);

	private OutputCollector outputCollector;

	protected static final ObjectMapper MAPPER = new ObjectMapper();

	public abstract void execute(Event event) throws Exception;

	@Override
	@SuppressWarnings("hiding")
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.outputCollector = outputCollector;
	}

	@Override
	public void execute(Tuple tuple) {
		try {
			Event event = (Event) tuple.getValueByField(Event.class.getSimpleName());
			execute(event);
		} catch (Exception e) {
			LOG.error("Exception caught in " + BaseEventEndBolt.class.getSimpleName() + ".execute()", e);
			outputCollector.reportError(e);
		}
		outputCollector.ack(tuple);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		//
	}

	@Override
	public void cleanup() {
		super.cleanup();
	}
}
