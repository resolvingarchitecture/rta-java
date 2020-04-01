package ra.rta.rfm.conspref.publish;

import java.util.List;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.BaseEventEmitterBolt;
import ra.rta.connectors.cassandra.CassandraMgr;
import ra.rta.Event;
import ra.rta.rfm.conspref.models.Record;

/**
 * Publish final results to Cassandra.
 *
 * Handles multiple records.
 */
public class PublishBolt extends BaseEventEmitterBolt {

	private static Logger LOG = LoggerFactory.getLogger(PublishBolt.class);

	private static final long serialVersionUID = 1L;

	private CassandraMgr cassandraMgr;

	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		super.prepare(map, topologyContext, outputCollector);
		cassandraMgr = CassandraMgr.init(map);
	}

	@Override
	public void execute(Event event) throws Exception {
		List<Record> records = (List<Record>)event.payload.get("records");

	}

}
