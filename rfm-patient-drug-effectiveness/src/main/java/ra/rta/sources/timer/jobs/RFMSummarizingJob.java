package ra.rta.sources.timer.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.sources.MessageManager;

/**
 * Created by Brian on 4/6/15.
 */
public class RFMSummarizingJob implements Job {

	private static final Logger LOG = LoggerFactory.getLogger(RFMSummarizingJob.class);

	public static final String KAFKA_TOPIC = "kafkaTopic";
	public static final String KAFKA_CONFIG = "kafkaConfig";
	public static final String KAFKA_PRODUCER = "kafkaProducer";

	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		String msg = "RFM Summarizing Job initiated...";
		LOG.info(msg);
		System.out.println(msg);
		MessageManager producer = (MessageManager) jobExecutionContext.getJobDetail().getJobDataMap().get(KAFKA_PRODUCER);
		String topic = (String) jobExecutionContext.getJobDetail().getJobDataMap().get(KAFKA_TOPIC);
		String body ="{ header : { command_path : summarize_start } }";
		// Send Summarizing Request
		try {
			producer.send(topic, body, false);
			msg = "RFM Summarizing Job service operation signal sent.";
			LOG.info(msg);
		} catch (Exception e) {
			msg = "Exception caught while running Summarizing Job: "+e;
			LOG.error(msg);
		}

	}
}
