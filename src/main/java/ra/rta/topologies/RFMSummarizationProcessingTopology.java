package ra.rta.topologies;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.Main;
import ra.rta.bolts.rfm.CustomerKPISplitterBolt;
import ra.rta.bolts.rfm.PartnerSplitterBolt;
import ra.rta.bolts.rfm.SummarizeCustomerRFMBolt;
import ra.rta.bolts.rfm.SummarizePartnerRFMBolt;

/**
 * Summarizes Partner and Customer KPI RFM values
 */
public class RFMSummarizationProcessingTopology extends Main {

	private static final Logger LOG = LoggerFactory.getLogger(RFMSummarizationProcessingTopology.class);

	@Override
	protected final void buildTopology() {
		LOG.info("Building {}...", RFMSummarizationProcessingTopology.class.getSimpleName());

		TopologyBuilder builder = new TopologyBuilder();

		// https://medium.com/@anyili0928/what-i-have-learned-from-kafka-partition-assignment-strategy-799fdf15d3ab
		spoutConf.getKafkaProps().put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, "org.apache.kafka.clients.consumer.RoundRobinAssignor");
		KafkaSpout<String,String> spout = new KafkaSpout<>(spoutConf);
		int kafkaParallelism = (int) config.get("topology." + name + ".kafka.parallelism");
		builder.setSpout(KafkaSpout.class.getSimpleName(), spout, kafkaParallelism * numberOfWorkers);

		builder.setBolt(PartnerSplitterBolt.class.getSimpleName(), new PartnerSplitterBolt(), 1).shuffleGrouping(KafkaSpout.class.getSimpleName());

		int customerKPISplitterParallelism = (Integer) config.get("topology." + name + ".customerKPIsplitter.parallelism");
		builder.setBolt(CustomerKPISplitterBolt.class.getSimpleName(), new CustomerKPISplitterBolt(), customerKPISplitterParallelism * numberOfWorkers)
		.shuffleGrouping(PartnerSplitterBolt.class.getSimpleName());

		int summarizeCustomerRFMParallelism = (Integer) config.get("topology." + name + ".summarizecustomerrfm.parallelism");
		builder.setBolt(SummarizeCustomerRFMBolt.class.getSimpleName(), new SummarizeCustomerRFMBolt(), summarizeCustomerRFMParallelism * numberOfWorkers)
		.localOrShuffleGrouping(CustomerKPISplitterBolt.class.getSimpleName());

		int summarizePartnerRFMParallelism = (Integer) config.get("topology." + name + ".summarizepartnerrfm.parallelism");
		builder.setBolt(SummarizePartnerRFMBolt.class.getSimpleName(), new SummarizePartnerRFMBolt(), summarizePartnerRFMParallelism * numberOfWorkers)
		.fieldsGrouping(SummarizeCustomerRFMBolt.class.getSimpleName(), new Fields("name", "termcode"));

		stormTopology =  builder.createTopology();

		LOG.info("{} built.", RFMSummarizationProcessingTopology.class.getSimpleName());
	}

}
