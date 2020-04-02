package ra.rta.connectors.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 */
public final class KafkaMgr {

    private Producer<String,byte[]> relaxedProducer;
    private Producer<String,byte[]> durableProducer;

    private static KafkaMgr instance;

    private KafkaMgr(){}

    public static KafkaMgr getInstance() {
        return instance;
    }

    public static KafkaMgr init(String[] args) {
        Map<String,Object> map = new HashMap<>();
        for(String arg :args){
            String[] tuple = arg.split("=");
            map.put(tuple[0].trim(), tuple[1].trim());
        }
        return init(map);
    }

    public static KafkaMgr init(Map<String,Object> map) {
        instance = new KafkaMgr();
        Properties rProps = new Properties();
        rProps.put(ProducerConfig.CLIENT_ID_CONFIG, "RelaxedProducer");
        rProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, map.get("topology.kafka.broker.list"));
        rProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        rProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        rProps.put(ProducerConfig.ACKS_CONFIG, "1");
//        rProps.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "ra.rta.producers.utilities.partitioners.KafkaRoundRobinPartitioner");
        instance.relaxedProducer = new KafkaProducer<>(rProps);

        Properties dProps = new Properties();
        dProps.put(ProducerConfig.CLIENT_ID_CONFIG, "DurableProducer");
        dProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, map.get("topology.kafka.broker.list"));
        dProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        dProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        dProps.put(ProducerConfig.ACKS_CONFIG, "-1");
//        dProps.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "ra.rta.producers.utilities.partitioners.KafkaRoundRobinPartitioner");
        instance.durableProducer = new KafkaProducer<>(dProps);
        return instance;
    }

    public void send(String topic, byte[] message, boolean durable) {
        if (durable)
            durableProducer.send(new ProducerRecord<>(topic, System.currentTimeMillis()+"", message));
        else
            relaxedProducer.send(new ProducerRecord<>(topic, System.currentTimeMillis()+"", message));
    }

    public void send(String topic, List<byte[]> messages, boolean durable) {
        long i = System.currentTimeMillis();
        if (durable) {
            for(byte[] msg : messages) {
                durableProducer.send(new ProducerRecord<>(topic, ""+i++, msg));
            }
        } else {
            for(byte[] msg : messages) {
                relaxedProducer.send(new ProducerRecord<>(topic, ""+i++, msg));
            }
        }
    }

    public void shutdown() {
        relaxedProducer.close();
        durableProducer.close();
    }

}
