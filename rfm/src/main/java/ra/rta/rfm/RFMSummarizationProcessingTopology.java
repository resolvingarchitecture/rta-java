package ra.rta.rfm;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.Main;
import ra.rta.rfm.bolts.IndividualKPISplitterBolt;
import ra.rta.rfm.bolts.GroupSplitterBolt;
import ra.rta.rfm.bolts.SummarizeIndividualRFMBolt;
import ra.rta.rfm.bolts.SummarizeGroupRFMBolt;

/**
 * Summarizes Partner and Customer KPI RFM values
 */
public class RFMSummarizationProcessingTopology extends Main {

	private static final Logger LOG = LoggerFactory.getLogger(ra.rta.RFMSummarizationProcessingTopology.class);

	@Override
	protected final void buildTopology() {
		LOG.info("Building {}...", ra.rta.RFMSummarizationProcessingTopology.class.getSimpleName());

		TopologyBuilder builder = new TopologyBuilder();

		// https://medium.com/@anyili0928/what-i-have-learned-from-kafka-partition-assignment-strategy-799fdf15d3ab
		spoutConf.getKafkaProps().put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, "org.apache.kafka.clients.consumer.RoundRobinAssignor");
		KafkaSpout<String,String> spout = new KafkaSpout<>(spoutConf);
		int kafkaParallelism = (int) config.get("topology." + name + ".kafka.parallelism");
		builder.setSpout(KafkaSpout.class.getSimpleName(), spout, kafkaParallelism * numberOfWorkers);

		builder.setBolt(GroupSplitterBolt.class.getSimpleName(), new GroupSplitterBolt(), 1).shuffleGrouping(KafkaSpout.class.getSimpleName());

		int customerKPISplitterParallelism = (Integer) config.get("topology." + name + ".customerKPIsplitter.parallelism");
		builder.setBolt(IndividualKPISplitterBolt.class.getSimpleName(), new IndividualKPISplitterBolt(), customerKPISplitterParallelism * numberOfWorkers)
		.shuffleGrouping(GroupSplitterBolt.class.getSimpleName());

		int summarizeCustomerRFMParallelism = (Integer) config.get("topology." + name + ".summarizecustomerrfm.parallelism");
		builder.setBolt(SummarizeIndividualRFMBolt.class.getSimpleName(), new SummarizeIndividualRFMBolt(), summarizeCustomerRFMParallelism * numberOfWorkers)
		.localOrShuffleGrouping(IndividualKPISplitterBolt.class.getSimpleName());

		int summarizePartnerRFMParallelism = (Integer) config.get("topology." + name + ".summarizepartnerrfm.parallelism");
		builder.setBolt(SummarizeGroupRFMBolt.class.getSimpleName(), new SummarizeGroupRFMBolt(), summarizePartnerRFMParallelism * numberOfWorkers)
		.fieldsGrouping(SummarizeIndividualRFMBolt.class.getSimpleName(), new Fields("name", "termcode"));

		stormTopology =  builder.createTopology();

		LOG.info("{} built.", ra.rta.RFMSummarizationProcessingTopology.class.getSimpleName());
	}

}
