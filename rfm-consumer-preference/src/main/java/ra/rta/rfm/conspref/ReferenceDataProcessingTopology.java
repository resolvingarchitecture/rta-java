package ra.rta.rfm.conspref;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.bolts.AnalyticsBolt;
import ra.rta.bolts.ClassificationBolt;
import ra.rta.bolts.ContentEnricherBolt;
import ra.rta.bolts.PublishBolt;
import ra.rta.bolts.TransformBolt;

/**
 *
 */
public class ReferenceDataProcessingTopology extends Main {

	private static final Logger LOG = LoggerFactory.getLogger(ReferenceDataProcessingTopology.class);

	@Override
	protected final void buildTopology() {
		LOG.info("Building {}...", ReferenceDataProcessingTopology.class.getSimpleName());

		TopologyBuilder builder = new TopologyBuilder();

		// https://medium.com/@anyili0928/what-i-have-learned-from-kafka-partition-assignment-strategy-799fdf15d3ab
		spoutConf.getKafkaProps().put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, "org.apache.kafka.clients.consumer.RoundRobinAssignor");
		KafkaSpout<String,String> spout = new KafkaSpout<>(spoutConf);
		int kafkaParallelism = (int) config.get("topology." + name + ".kafka.parallelism");
		builder.setSpout(KafkaSpout.class.getSimpleName(), spout, kafkaParallelism * numberOfWorkers);

		int transformParallelism = (int) config.get("topology." + name + ".transform.parallelism");
		builder
		.setBolt(TransformBolt.class.getSimpleName(), new TransformBolt(), transformParallelism * numberOfWorkers)
		.localOrShuffleGrouping(KafkaSpout.class.getSimpleName());

		int contentEnricherParallelism = (int) config.get("topology." + name + ".contentenricher.parallelism");
		builder
		.setBolt(ContentEnricherBolt.class.getSimpleName(), new ContentEnricherBolt(), contentEnricherParallelism * numberOfWorkers)
		.localOrShuffleGrouping(TransformBolt.class.getSimpleName());

		int analyticsParallelism = (int) config.get("topology." + name + ".analytics.parallelism");
		String kBaseName = (String) config.get("topology." + name + ".rules.kBaseName");
		config.put("topology.rules.kBaseName", kBaseName);
		builder
		.setBolt(AnalyticsBolt.class.getSimpleName(), new AnalyticsBolt(), analyticsParallelism * numberOfWorkers)
		.localOrShuffleGrouping(ContentEnricherBolt.class.getSimpleName());

		int classificationParallelism = (int) config.get("topology." + name + ".classification.parallelism");
		builder
		.setBolt(ClassificationBolt.class.getSimpleName(), new ClassificationBolt(), classificationParallelism * numberOfWorkers)
		.localOrShuffleGrouping(AnalyticsBolt.class.getSimpleName());

		int publishParallelism = (int) config.get("topology." + name + ".publish.parallelism");
		builder
		.setBolt(PublishBolt.class.getSimpleName(), new PublishBolt(), publishParallelism * numberOfWorkers)
		.localOrShuffleGrouping(ClassificationBolt.class.getSimpleName());

		stormTopology = builder.createTopology();

		LOG.info("{} built.", ReferenceDataProcessingTopology.class.getSimpleName());
	}
}
