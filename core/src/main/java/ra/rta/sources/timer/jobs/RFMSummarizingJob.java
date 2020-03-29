package ra.rta.rfm.conspref.sources.timer.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.rfm.conspref.models.Event;
import ra.rta.rfm.conspref.sources.SourceConfig;
import ra.rta.rfm.conspref.sources.timer.TimerSource;
import ra.rta.rfm.conspref.utilities.JSONUtil;
import ra.rta.rfm.conspref.utilities.RandomUtil;

/**
 * Sends an event to kick off RFM Summarizing
 */
public class RFMSummarizingJob implements Job {

	private static final Logger LOG = LoggerFactory.getLogger(RFMSummarizingJob.class);

	private SourceConfig sourceConfig;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		String msg = "RFM Summarizing Job initiated...";
		LOG.info(msg);
		System.out.println(msg);
		SourceConfig c = (SourceConfig)jobExecutionContext.getJobDetail().getJobDataMap().get(TimerSource.CONFIG);
		Event event = new Event();
		event.groupId = 1;
		event.id = RandomUtil.nextRandomLong();
		event.command = 1001;
		event.save = true;
		// Send Summarizing Request
		try {
			c.messageManager.send(c.topic, JSONUtil.MAPPER.writeValueAsBytes(event), false);
			msg = "RFM Summarizing Job service operation signal sent.";
			LOG.info(msg);
		} catch (Exception e) {
			msg = "Exception caught while running Summarizing Job: "+e;
			LOG.error(msg);
		}

	}
}
