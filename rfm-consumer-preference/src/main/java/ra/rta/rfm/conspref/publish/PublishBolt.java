package ra.rta.rfm.conspref.publish;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.EventEndBolt;
import ra.rta.connectors.cassandra.CassandraMgr;
import ra.rta.Event;

/**
 * Publish final results to Cassandra.
 *
 * Handles multiple records.
 */
public class PublishBolt extends EventEndBolt {

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

	}

}
