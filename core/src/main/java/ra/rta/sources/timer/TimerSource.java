package ra.rta.sources.timer;

import org.quartz.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.MessageManager;
import ra.rta.sources.SourceConfig;
import ra.rta.utilities.RandomUtil;

/**
 * Schedules Jobs.
 */
public class TimerSource implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(TimerSource.class);

	public static final String CONFIG = "config";
	private SourceConfig sourceConfig;

	private Scheduler scheduler;

	public static void main(String[] args) {
		SourceConfig c = new SourceConfig();
		int i=0;
		// Set up props
		c.props.put("metadata.broker.list", args[i++]);
		try {
			c.jobClass = (Class<? extends Job>) Class.forName(args[i++]);
		} catch (ClassNotFoundException e) {
			LOG.error(e.getLocalizedMessage());
			System.exit(-1);
		}
		c.topic = args[i++];
		c.periodicity = args[i++];
		c.timeDivision1 = Integer.parseInt(args[i++]);
		c.timeDivision2 = Integer.parseInt(args[i++]);
		c.props.put("topology.cassandra.seednode",args[i++]);
		c.messageManager = new MessageManager(c.props);

		TimerSource timerSource = new TimerSource(c);
		timerSource.run();
	}

	public TimerSource(SourceConfig sourceConfig) {
		this.sourceConfig = sourceConfig;
	}

	@Override
	public void run() {
		LOG.info(TimerSource.class.getSimpleName() + " starting with the following parameters:");
		LOG.info("Kafka IP: " + sourceConfig.props.get("metadata.broker.list"));

		// Setup Quartz Timer
		SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
		try {
			scheduler = schedFact.getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			LOG.error(e.getLocalizedMessage());
			return;
		}

		// Schedule Summarizing Job
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(TimerSource.CONFIG, sourceConfig);

		JobDetail job = JobBuilder.newJob(sourceConfig.jobClass)
					.withIdentity(sourceConfig.jobClass.getName()+"-"+ RandomUtil.nextRandomInteger())
					.usingJobData(jobDataMap)
					.build();

		Trigger trigger;
		try {
			switch (sourceConfig.periodicity) {
				case "Monthly": {
					trigger = TriggerBuilder.newTrigger()
							.withSchedule(CronScheduleBuilder.monthlyOnDayAndHourAndMinute(sourceConfig.timeDivision1, sourceConfig.timeDivision2, sourceConfig.timeDivision3))
							.build();
					scheduler.scheduleJob(job, trigger); break;
				}
				case "Weekly": {
					trigger = TriggerBuilder.newTrigger()
							.withSchedule(CronScheduleBuilder.weeklyOnDayAndHourAndMinute(sourceConfig.timeDivision1, sourceConfig.timeDivision2, sourceConfig.timeDivision3))
							.build();
					scheduler.scheduleJob(job, trigger); break;
				}
				case "DaysOfWeek": {
					trigger = TriggerBuilder.newTrigger()
							.withSchedule(CronScheduleBuilder.atHourAndMinuteOnGivenDaysOfWeek(sourceConfig.timeDivision1, sourceConfig.timeDivision2, sourceConfig.timeDivision4))
							.build();
					scheduler.scheduleJob(job, trigger); break;
				}
				case "Daily": {
					trigger = TriggerBuilder.newTrigger()
							.withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(sourceConfig.timeDivision1, sourceConfig.timeDivision2))
							.build();
					scheduler.scheduleJob(job, trigger); break;
				}
				case "Minutely": {
					trigger = TriggerBuilder.newTrigger()
							.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(sourceConfig.timeDivision1))
							.build();
					scheduler.scheduleJob(job, trigger); break;
				}
				case "Secondly": {
					trigger = TriggerBuilder.newTrigger()
							.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(sourceConfig.timeDivision1))
							.build();
					scheduler.scheduleJob(job, trigger); break;
				}
			}
		} catch (SchedulerException e) {
			LOG.error(e.getLocalizedMessage());
			return;
		}

		LOG.info("Job Scheduled.");
	}
}
