package ra.rta.topologies.bolts.rfm;

import java.util.*;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import ra.rta.models.utilities.DateUtility;

/**
 * Gets unique list of Customer KPIs tracked and splits on them
 */
public class CustomerKPISplitterBolt extends BaseRichBolt {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(CustomerKPISplitterBolt.class);

    private OutputCollector outputCollector;

    private Cluster cluster;
    private Session session;

    @Override
    @SuppressWarnings("hiding")
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
        // Cassandra preparation
        String cassandraNode = (String)map.get("topology.cassandra.seednode");
        cluster = Cluster.builder().addContactPoint(cassandraNode).build();
        Metadata metadata = cluster.getMetadata();
        LOG.info("{} Connected to cluster: {}", CustomerKPISplitterBolt.class.getSimpleName(),
                metadata.getClusterName());
        for (Host host : metadata.getAllHosts()) {
            LOG.info("{} Datacenter: {} ; Host: {} Rack: {}", CustomerKPISplitterBolt.class.getSimpleName(),
                    host.getDatacenter(), host.getAddress(), host.getRack());
        }
        session = cluster.connect();
    }

    @Override
    public void execute(Tuple tuple) {
        LOG.info("Started {} execution...", CustomerKPISplitterBolt.class.getSimpleName());
        String partnerName = tuple.getStringByField("name");
        try {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.DATE, -366);
            int beginningWindowDateInt = DateUtility.dateToInt(now.getTime());
            LOG.info("beginWindowDateInt: "+beginningWindowDateInt);
            ResultSet rs = session.execute(new SimpleStatement("SELECT adid, termcode, date FROM " + partnerName + ".customer_KPI_frequency;"));
            long totalSeen = 0;
            long totalWithinWindow = 0;
            long totalEmitted = 0;
            String hash = null;
            for (Row row : rs) {
                int date = row.getInt("date");
                if (date > beginningWindowDateInt) {
                    UUID adid = row.getUUID("adid");
                    int termcode = row.getInt("termcode");
                    String nextHash = adid.toString()+"|"+termcode;
                    if (!nextHash.equals(hash)) {
                        outputCollector.emit(new Values(partnerName, adid, termcode));
                    }
                    hash = nextHash;
                }
                totalSeen++;
            }
            LOG.info("CustomerKPISplitter Totals: seen="+totalSeen+"; within window="+totalWithinWindow+"; emitted="+totalEmitted);
        } catch (Exception e) {
            LOG.error(CustomerKPISplitterBolt.class.getSimpleName() + " error: " + e);
            outputCollector.reportError(e);
        }
        outputCollector.ack(tuple);
        LOG.info("{} execution completed.", CustomerKPISplitterBolt.class.getSimpleName());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("name", "adid", "termcode"));
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

}