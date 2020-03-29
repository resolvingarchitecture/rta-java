package ra.rta.rfm.bolts;

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/**
 * Split Partners by name
 */
public class GroupSplitterBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(GroupSplitterBolt.class);

	private OutputCollector outputCollector;

	private Cluster cluster;
	private Session session;

	private BoundStatement groupLookupStatement;

	@Override
	@SuppressWarnings("hiding")
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.outputCollector = outputCollector;
		// Cassandra preparation
		String cassandraNode = (String)map.get("topology.cassandra.seednode");
		cluster = Cluster.builder().addContactPoint(cassandraNode).build();
		Metadata metadata = cluster.getMetadata();
		LOG.info("{} Connected to cluster: {}", GroupSplitterBolt.class.getSimpleName(), metadata.getClusterName());
		for (Host host : metadata.getAllHosts()) {
			LOG.info("{} Datacenter: {}; Host: {}; Rack: {}", GroupSplitterBolt.class.getSimpleName(),
					host.getDatacenter(), host.getListenAddress(), host.getRack());
		}
		session = cluster.connect();
		groupLookupStatement = new BoundStatement(session.prepare("SELECT name FROM ra.group;"));
	}

	@Override
	public void execute(Tuple tuple) {
		LOG.info("Started {} execution...", GroupSplitterBolt.class.getSimpleName());
		try {
			// Get list of group names and split on them
			ResultSet rs = session.execute(groupLookupStatement);
			for (Row row : rs) {
				String groupName = row.getString(0);
				LOG.info("Group.name: " + groupName);
				outputCollector.emit(new Values(groupName));
			}

		} catch (Exception e) {
			//            outputCollector.fail(tuple);
			LOG.error(GroupSplitterBolt.class.getSimpleName() + " error: " + e);
			outputCollector.reportError(e);
		}

		outputCollector.ack(tuple);
		LOG.info("{} execution completed.", GroupSplitterBolt.class.getSimpleName());
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("name"));
	}

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Map<String,Object> config = new HashMap<>();
        config.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 24 * 60 * 60); // Once Daily
        return config;
    }

    @Override
	public void cleanup() {
		super.cleanup();
		cluster.close();
	}
}
