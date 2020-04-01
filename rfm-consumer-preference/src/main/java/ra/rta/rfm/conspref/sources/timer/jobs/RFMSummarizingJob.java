package ra.rta.rfm.conspref.sources.timer.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.Event;
import ra.rta.connectors.kafka.KafkaMgr;
import ra.rta.utilities.JSONUtil;

/**
 *
 */
public class RFMSummarizingJob implements Job {

	private static final Logger LOG = LoggerFactory.getLogger(RFMSummarizingJob.class);

	public static final String KAFKA_TOPIC = "kafkaTopic";
	public static final String KAFKA_MGR = "kafkaMgr";

	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		String msg = "RFM Summarizing Job initiated...";
		LOG.info(msg);
		System.out.println(msg);
		KafkaMgr mgr = (KafkaMgr)jobExecutionContext.getMergedJobDataMap().get(KAFKA_MGR);
		String topic = (String)jobExecutionContext.getMergedJobDataMap().get(KAFKA_TOPIC);
		Event event = new Event();
		event.commandId = 1500;
		// Send Summarizing Request
		try {
			mgr.send(topic, JSONUtil.MAPPER.writeValueAsBytes(event), false);
			msg = "RFM Summarizing Job service operation signal sent.";
			LOG.info(msg);
		} catch (Exception e) {
			msg = "Exception caught while running Summarizing Job: "+e;
			LOG.error(msg);
		}

	}
}
