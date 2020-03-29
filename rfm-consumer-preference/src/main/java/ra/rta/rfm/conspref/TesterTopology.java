package ra.rta.rfm.conspref;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;
import ra.rta.rfm.conspref.test.TesterBolt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class TesterTopology extends Main {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionProcessingTopology.class);

    @Override
    protected final void buildTopology() {

        LOG.info("Building {}...", TesterTopology.class.getSimpleName());

        TopologyBuilder builder = new TopologyBuilder();

        // https://medium.com/@anyili0928/what-i-have-learned-from-kafka-partition-assignment-strategy-799fdf15d3ab
        spoutConf.getKafkaProps().put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, "org.apache.kafka.clients.consumer.RoundRobinAssignor");
        KafkaSpout<String,String> spout = new KafkaSpout<>(spoutConf);
        int kafkaParallelism = (int) config.get("topology." + name + ".kafka.parallelism");
        builder.setSpout(KafkaSpout.class.getSimpleName(), spout, kafkaParallelism * numberOfWorkers);

        int testerParallelism = (Integer) config.get("topology." + name + ".tester.parallelism");
        builder.setBolt(TesterBolt.class.getSimpleName(), new TesterBolt(), testerParallelism * numberOfWorkers)
                .localOrShuffleGrouping(KafkaSpout.class.getSimpleName());

        stormTopology =  builder.createTopology();

        LOG.info(TesterTopology.class.getSimpleName() + " built.");

    }
}
