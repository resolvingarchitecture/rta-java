package ra.rta.topologies;


public class LocalRunner {

//	public void run(String topologyName, Config config, StormTopology topology) throws Exception {
//		TestingServer zkServer = new TestingServer(2181, true);
//		Properties props = new Properties();
//		props.setProperty("zookeeper.connect", zkServer.getConnectString());
//		props.setProperty("broker.id", "1");
//		KafkaConfig kafkaConfig = new KafkaConfig(props);
//		KafkaServerStartable kafkaServer = new KafkaServerStartable(kafkaConfig);
//		kafkaServer.startup();
//		LocalCluster stormCluster = new LocalCluster();
//		ZkClient client = new ZkClient(zkServer.getConnectString());
//		client.createPersistent("/brokers/topics/" + topologyName + "/partitions", true);
//		client.close();
//		stormCluster.submitTopology(topologyName, config, topology);
//	}
}
