package ra.rta.producers.timer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.producers.timer.jobs.RFMSummarizingJob;

/**
 * Schedules Jobs.
 */
public class TimerProducer {

    private static final Logger LOG = LoggerFactory.getLogger(TimerProducer.class);

	private Scheduler scheduler;

	public static void main(String[] args) throws Exception {
		TimerProducer timerProducer = new TimerProducer();
		// Kafka Properties
		Properties props = new Properties();
		// Set up props
		props.put("metadata.broker.list", args[0]);
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("request.required.acks", "1");

		String kafkaTopic = args[1];
		String periodicity = args[2];
		int timeDivision1 = Integer.parseInt(args[3]);
		int timeDivision2 = args[4] == null ? 0 : Integer.parseInt(args[4]);
        Map<String,String> properties = new HashMap<>();
        properties.put("topology.cassandra.seednode",args[5]);
//        DataServiceManager dataServiceManager = new DataServiceManager(properties);
//        String wandPostPath = args[6];

		LOG.info(TimerProducer.class.getSimpleName() + " starting with the following parameters:");
        LOG.info("Kafka IP: " + args[0]);

		// Create Kafka Producer
		Producer<String, String> kafkaProducer = new KafkaProducer(properties);

		// Setup Quartz Timer
		SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
		timerProducer.scheduler = schedFact.getScheduler();
		timerProducer.scheduler.start();

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
//        timerProducer.scheduler.scheduleJob(suspendReprocessJob, suspendReprocessJobTrigger);

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
//        timerProducer.scheduler.scheduleJob(wandPostJob, wandPostJobTrigger);

		// Schedule Summarizing Job
		JobDataMap summarizingJobDataMap = new JobDataMap();
		summarizingJobDataMap.put(RFMSummarizingJob.KAFKA_TOPIC, kafkaTopic);
		summarizingJobDataMap.put(RFMSummarizingJob.KAFKA_CONFIG, properties);
		summarizingJobDataMap.put(RFMSummarizingJob.KAFKA_PRODUCER, kafkaProducer);

		JobDetail summarizingJob = JobBuilder.newJob(RFMSummarizingJob.class)
				.withIdentity(RFMSummarizingJob.class.getSimpleName())
				.usingJobData(summarizingJobDataMap)
				.build();

		Trigger summarizingJobTrigger;
		switch (periodicity) {
            case "Daily": {
                summarizingJobTrigger = TriggerBuilder.newTrigger()
                        .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(timeDivision1, timeDivision2))
                        .build();
                timerProducer.scheduler.scheduleJob(summarizingJob, summarizingJobTrigger); break;
            }
            case "Minutely": {
                summarizingJobTrigger = TriggerBuilder.newTrigger()
                        .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(timeDivision1))
                        .build();
                timerProducer.scheduler.scheduleJob(summarizingJob, summarizingJobTrigger); break;
            }
		}

        LOG.info("Jobs Scheduled.");
	}
}
