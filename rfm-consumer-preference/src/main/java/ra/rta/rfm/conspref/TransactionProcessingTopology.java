package ra.rta.rfm.conspref;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.Main;
import ra.rta.analyze.DroolsBolt;
import ra.rta.rfm.conspref.classify.WANDClassificationBolt;
import ra.rta.rfm.conspref.enrich.EnricherBolt;
import ra.rta.rfm.conspref.publish.PublishBolt;
import ra.rta.transform.TransformBolt;

/**
 * Process transactions.
 */
public class TransactionProcessingTopology extends Main {

	private static final Logger LOG = LoggerFactory.getLogger(TransactionProcessingTopology.class);

	@Override
	protected final void buildTopology() {
		LOG.info("Building {}...", TransactionProcessingTopology.class.getSimpleName());

		TopologyBuilder builder = new TopologyBuilder();

		// https://medium.com/@anyili0928/what-i-have-learned-from-kafka-partition-assignment-strategy-799fdf15d3ab
		spoutConf.getKafkaProps().put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, "org.apache.kafka.clients.consumer.RoundRobinAssignor");
		KafkaSpout<String,String> spout = new KafkaSpout<>(spoutConf);
		int kafkaParallelism = (int) config.get("topology." + name + ".kafka.parallelism");
		builder.setSpout(KafkaSpout.class.getSimpleName(), spout, kafkaParallelism * numberOfWorkers);

		int transformParallelism = (Integer) config.get("topology." + name + ".transform.parallelism");
		builder.setBolt(TransformBolt.class.getSimpleName(), new TransformBolt(), transformParallelism * numberOfWorkers)
		.localOrShuffleGrouping(KafkaSpout.class.getSimpleName());

		int classificationParallelism = (Integer) config.get("topology." + name + ".classification.parallelism");
		builder.setBolt(WANDClassificationBolt.class.getSimpleName(), new WANDClassificationBolt(), classificationParallelism * numberOfWorkers)
		.fieldsGrouping(TransformBolt.class.getSimpleName(), new Fields("description"));

		int contentEnricherParallelism = (Integer) config.get("topology." + name + ".enricher.parallelism");
		builder.setBolt(EnricherBolt.class.getSimpleName(), new EnricherBolt(), contentEnricherParallelism * numberOfWorkers)
		.localOrShuffleGrouping(WANDClassificationBolt.class.getSimpleName());

		int analyticsParallelism = (Integer) config.get("topology." + name + ".analytics.parallelism");
		String kBaseName = (String) config.get("topology." + name + ".rules.kBaseName");
		config.put("topology.rules.kBaseName", kBaseName);
		builder.setBolt(DroolsBolt.class.getSimpleName(), new DroolsBolt(), analyticsParallelism * numberOfWorkers)
		.localOrShuffleGrouping(EnricherBolt.class.getSimpleName());

		int publishParallelism = (Integer) config.get("topology." + name + ".publish.parallelism");
		builder.setBolt(PublishBolt.class.getSimpleName(), new PublishBolt(), publishParallelism * numberOfWorkers)
		.localOrShuffleGrouping(DroolsBolt.class.getSimpleName());

		stormTopology =  builder.createTopology();

		LOG.info(TransactionProcessingTopology.class.getSimpleName() + " built.");

	}

}
