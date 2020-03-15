package ra.rta.bolts.rfm;

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
public class PartnerSplitterBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(PartnerSplitterBolt.class);

	private OutputCollector outputCollector;

	private Cluster cluster;
	private Session session;

	private BoundStatement partnerLookupStatement;

	@Override
	@SuppressWarnings("hiding")
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.outputCollector = outputCollector;
		// Cassandra preparation
		String cassandraNode = (String)map.get("topology.cassandra.seednode");
		cluster = Cluster.builder().addContactPoint(cassandraNode).build();
		Metadata metadata = cluster.getMetadata();
		LOG.info("{} Connected to cluster: {}", PartnerSplitterBolt.class.getSimpleName(), metadata.getClusterName());
		for (Host host : metadata.getAllHosts()) {
			LOG.info("{} Datacenter: {}; Host: {}; Rack: {}", PartnerSplitterBolt.class.getSimpleName(),
					host.getDatacenter(), host.getAddress(), host.getRack());
		}
		session = cluster.connect();
		partnerLookupStatement = new BoundStatement(session.prepare("SELECT name FROM ra.partner;"));
	}

	@Override
	public void execute(Tuple tuple) {
		LOG.info("Started {} execution...", PartnerSplitterBolt.class.getSimpleName());
		try {
			// Get list of Partner names and split on them
			ResultSet rs = session.execute(partnerLookupStatement);
			for (Row row : rs) {
				String partnerName = row.getString(0);
				LOG.info("Partner.name: " + partnerName);
				outputCollector.emit(new Values(partnerName));
			}

		} catch (Exception e) {
			//            outputCollector.fail(tuple);
			LOG.error(PartnerSplitterBolt.class.getSimpleName() + " error: " + e);
			outputCollector.reportError(e);
		}

		outputCollector.ack(tuple);
		LOG.info("{} execution completed.", PartnerSplitterBolt.class.getSimpleName());
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
