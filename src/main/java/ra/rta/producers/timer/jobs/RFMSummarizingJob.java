package ra.rta.producers.timer.jobs;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Brian on 4/6/15.
 */
public class RFMSummarizingJob implements Job {

	private static final Logger LOG = LoggerFactory.getLogger(RFMSummarizingJob.class);

	public static final String KAFKA_TOPIC = "kafkaTopic";
	public static final String KAFKA_CONFIG = "kafkaConfig";
	public static final String KAFKA_PRODUCER = "kafkaProducer";

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		String msg = "RFM Summarizing Job initiated...";
		LOG.info(msg);
		System.out.println(msg);
		Producer producer = (Producer) jobExecutionContext.getJobDetail().getJobDataMap().get(KAFKA_PRODUCER);
		String topic = (String) jobExecutionContext.getJobDetail().getJobDataMap().get(KAFKA_TOPIC);
		String body ="{ header : { command_path : summarize_start } }";
		// Send Summarizing Request
		try {
			ProducerRecord<String,String> message = new ProducerRecord<>(topic,body);
			producer.send(message);
			msg = "RFM Summarizing Job service operation signal sent.";
			LOG.info(msg);
			System.out.println(msg);
		} catch (Exception e) {
			e.printStackTrace();
			msg = "Exception caught while running Summarizing Job: "+e;
			LOG.error(msg);
			System.out.println(msg);
		}

	}
}
