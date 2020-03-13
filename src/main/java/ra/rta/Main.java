package ra.rta;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import kafka.server.KafkaConfig;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
/**
 *
 */
public class Main {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	protected String stormConfigFileLocation;
	protected String name;
	protected String topic;
	protected String zkRoot;
	protected String id;
	protected KafkaSpoutConfig<String,String> spoutConf;
	protected StormTopology stormTopology;
	protected Config config;
	protected KafkaConfig kafkaConfig;
	protected int numberOfWorkers;

	public static void main(String[] args) {
		Main topology;
		String stormConfigFileLocation = args[0];
		String name = args[1];
		boolean local = false;
		if(args.length > 2) {
			local = args[2] != null && "local".equals(args[2]);
		}
		if(name==null) {
			throw new RuntimeException("Must provide a topology name.");
		}
		try {
			Config config = loadConfig(stormConfigFileLocation);
			String mainClass = (String)config.get("topology."+name+".main");
			topology = (Main)Class.forName(mainClass).getConstructor().newInstance();
			topology.config = config;
			topology.numberOfWorkers = (Integer)topology.config.get("topology."+name+".workers");
			topology.config.setNumWorkers(topology.numberOfWorkers);
			topology.stormConfigFileLocation = stormConfigFileLocation;
			topology.name = name;
			topology.topic = (String)config.get("topology."+name+".topic");
			topology.zkRoot = "/"+topology.topic;
			topology.id = UUID.randomUUID().toString();
			topology.spoutConf = KafkaSpoutConfig
					.builder("127.0.0.1", topology.topic)
					.setProcessingGuarantee(KafkaSpoutConfig.ProcessingGuarantee.AT_LEAST_ONCE)
					.setTupleTrackingEnforced(true)
					.build();
//			topology.spoutConf = new KafkaSpoutConfig(topology.brokerHosts, topology.topic, topology.zkRoot, topology.id);
			//            topology.spoutConf.scheme =  new SchemeAsMultiScheme(new StringScheme());
			topology.buildTopology(); // Topology-specific
			topology.start(local);
		} catch (InstantiationException e) {
			LOG.error("Unable to instantiate main class.");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			LOG.error("Do not have OS access rights to load main class.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			LOG.error("Class not found. Please configure a supported Topology class.");
			e.printStackTrace();
		} catch (Exception e) {
			LOG.error("General Exception caught while attempting to deploy topology.");
			e.printStackTrace();
		}
	}

	protected void buildTopology() throws Exception {
		LOG.info("Main.buildTopology() not implemented.");
	}

	protected void start(boolean local) {
		try {
			if (!local) {
				StormSubmitter.submitTopology(name, config, stormTopology);
			} else {
				//                spoutConf.zkServers = new ArrayList<>(1);
				//                spoutConf.zkServers.add("localhost");
				//                spoutConf.zkPort = 2181;
				//                config.put("storm.zookeeper.servers",spoutConf.zkServers);
				//                config.put("storm.zookeeper.port",2181);
				//                config.setDebug(true);
				//                config.setNumWorkers(1);
				//                config.setMaxSpoutPending(10);
				//                config.setMessageTimeoutSecs(5 * 60);
				//                config.put("topology.cassandra.seednode", "127.0.0.1");
				//                config.put("topology.kafka.broker.list","localhost:9092");
				//                config.put("topology.data.wand.chase", "/Library/apache-storm-0.9.3/data/wand/wand_chase_lookup.csv");
				//                config.put("topology.data.transforms.formats","/Library/apache-storm-0.9.3/data/transforms/formats.json");
				//                config.put("topology.data.rules","/Library/apache-storm-0.9.3/data/rules");
//				LocalCluster cluster = new LocalCluster("localhost", 2181L);
//				cluster.submitTopology(topic, config, stormTopology);
				//                Utils.sleep(3 * 60 * 1000);
				//                cluster.killTopology(name);
				//                cluster.shutdown();
			}
		} catch(Exception e) {
			LOG.error("Exception caught in " + Main.class.getName(), e);
		}
	}

	private static Config loadConfig(String stormConfigFileLocation) throws Exception {
		Yaml yaml = new Yaml();
		try (InputStream ios = new FileInputStream(new File(stormConfigFileLocation));) {
			// Parse the YAML file
			Map<String, Object> result = (Map<String, Object>) yaml.load(ios);
			LOG.info("Storm Configuration Settings:\n" + result.toString());
			Config config = new Config();
			config.putAll(result);
			return config;
		}
	}

}
