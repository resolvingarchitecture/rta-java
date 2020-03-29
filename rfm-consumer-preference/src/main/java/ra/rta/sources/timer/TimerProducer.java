package ra.rta.sources.timer;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.quartz.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.sources.MessageManager;
import ra.rta.sources.timer.jobs.RFMSummarizingJob;

/**
 * Schedules Jobs.
 */
public class TimerProducer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(TimerProducer.class);

	private Scheduler scheduler;
	private MessageManager messageManager;
	private Map<String,Object> props;

	public static void main(String[] args) {
		// Kafka Properties
		Map<String,Object> props = new HashMap<>();
		// Set up props
		props.put("metadata.broker.list", args[0]);
		props.put("topic", args[1]);
		props.put("periodicity", args[2]);
		props.put("timeDivision1", Integer.parseInt(args[3]));
		props.put("timeDivision2", Integer.parseInt(args[4]));
        props.put("topology.cassandra.seednode",args[5]);
        props.put("wandsPostPath", args[6]);

		TimerProducer timerProducer = new TimerProducer(props);
		timerProducer.run();
	}

	public TimerProducer(Map<String,Object> properties) {
		props = properties;
	}

	@Override
	public void run() {
		LOG.info(TimerProducer.class.getSimpleName() + " starting with the following parameters:");
		LOG.info("Kafka IP: " + props.get("metadata.broker.list"));

		String kafkaTopic = (String)props.get("topic");
		String periodicity = (String)props.get("periodicity");
		int timeDivision1 = (Integer)props.get("timeDivision1");
		int timeDivision2 = (Integer)props.get("timeDivision2");
		messageManager = new MessageManager(props);

		// Setup Quartz Timer
		SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
		try {
			scheduler = schedFact.getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			LOG.error(e.getLocalizedMessage());
			return;
		}

		// Schedule Suspend Reprocess Job
//        JobDataMap suspendReprocessJobDataMap = new JobDataMap();
//        suspendReprocessJobDataMap.put(DataServiceManager.class.getSimpleName(), dataServiceManager);
//        suspendReprocessJobDataMap.put(SuspendReprocessJob.KAFKA_TOPIC, "transaction");
//        suspendReprocessJobDataMap.put(SuspendReprocessJob.KAFKA_CONFIG, kafkaConfig);
//        suspendReprocessJobDataMap.put(SuspendReprocessJob.KAFKA_PRODUCER, kafkaProducer);
//
//        JobDetail suspendReprocessJob = JobBuilder.newJob(SuspendReprocessJob.class)
//                .withIdentity(SuspendReprocessJob.class.getSimpleName())
//                .usingJobData(suspendReprocessJobDataMap)
//                .build();
//
//        Trigger suspendReprocessJobTrigger = TriggerBuilder.newTrigger()
//                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(4, 30))
//                .build();
//
//        scheduler.scheduleJob(suspendReprocessJob, suspendReprocessJobTrigger);

		// Schedule WAND Post Job
//        JobDataMap wandPostJobDataMap = new JobDataMap();
//        wandPostJobDataMap.put(DataServiceManager.class.getSimpleName(), dataServiceManager);
//        wandPostJobDataMap.put("wandPostPath",wandPostPath);
//
//        JobDetail wandPostJob = JobBuilder.newJob(WANDPostJob.class)
//                .withIdentity(WANDPostJob.class.getSimpleName())
//                .usingJobData(wandPostJobDataMap)
//                .build();
//
//        Trigger wandPostJobTrigger = TriggerBuilder.newTrigger()
//                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0,30))
//                .build();
//
//        scheduler.scheduleJob(wandPostJob, wandPostJobTrigger);

		// Schedule Summarizing Job
		JobDataMap summarizingJobDataMap = new JobDataMap();
		summarizingJobDataMap.put(RFMSummarizingJob.KAFKA_TOPIC, kafkaTopic);
		summarizingJobDataMap.put(RFMSummarizingJob.KAFKA_CONFIG, props);
		summarizingJobDataMap.put(RFMSummarizingJob.KAFKA_PRODUCER, messageManager);

		JobDetail summarizingJob = JobBuilder.newJob(RFMSummarizingJob.class)
				.withIdentity(RFMSummarizingJob.class.getSimpleName())
				.usingJobData(summarizingJobDataMap)
				.build();

		Trigger summarizingJobTrigger;
		try {
			switch (periodicity) {
				case "Daily": {
					summarizingJobTrigger = TriggerBuilder.newTrigger()
							.withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(timeDivision1, timeDivision2))
							.build();
					scheduler.scheduleJob(summarizingJob, summarizingJobTrigger); break;
				}
				case "Minutely": {
					summarizingJobTrigger = TriggerBuilder.newTrigger()
							.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(timeDivision1))
							.build();
					scheduler.scheduleJob(summarizingJob, summarizingJobTrigger); break;
				}
			}
		} catch (SchedulerException e) {
			LOG.error(e.getLocalizedMessage());
			return;
		}

		LOG.info("Jobs Scheduled.");
	}
}
