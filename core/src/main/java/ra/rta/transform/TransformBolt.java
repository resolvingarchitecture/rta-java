package ra.rta.transform;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.models.Event;
import ra.rta.utilities.JSONUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Transform the raw data into internal data structure in Java format.
 */
public class TransformBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(TransformBolt.class);

	private Map map;
	private TopologyContext topologyContext;
	private OutputCollector outputCollector;
	private TransformerFactory transformerFactory = new TransformerFactory();

	@Override
	@SuppressWarnings("hiding")
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.map = map;
		this.topologyContext = topologyContext;
		this.outputCollector = outputCollector;
		File xformMapFile = new File((String)map.get("topology.transform.mappingfile"));
		if(xformMapFile.exists()) {
			try {
				transformerFactory.transformMap = JSONUtil.MAPPER.readValue(xformMapFile, Map.class);
			} catch (IOException e) {
				LOG.warn(e.getLocalizedMessage());
			}
		}
	}

	@Override
	public void execute(Tuple tuple) {
		byte[] raw = tuple.getBinary(0);
		try {
			Event event = JSONUtil.MAPPER.readValue(raw, Event.class);
			try {
				Transformer transformer = transformerFactory.build(event);
				transformer.transform(event);
				outputCollector.emit(tuple, new Values(event.id,event));
			} catch (Exception e) {
				LOG.warn("Exception caught in " + TransformBolt.class.getSimpleName() + ".execute()", e);
				outputCollector.reportError(e);
			}
		} catch (Exception e) {
			LOG.warn("Exception caught attempting to read Event from JSON stream: {}", new String(raw));
			outputCollector.reportError(e);
		}
		outputCollector.ack(tuple);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("description", Event.class.getSimpleName()));
	}

	@Override
	public void cleanup() {
		super.cleanup();
	}

}
