package it.bioko.system.service.cron;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.impl.matchers.KeyMatcher.keyEquals;
import it.bioko.system.KILL_ME.exception.SystemException;
import it.bioko.system.command.Command;
import it.bioko.system.context.Context;
import it.bioko.system.service.cron.quartz.CommandRunnerJob;
import it.bioko.system.service.cron.quartz.QuartzCronListenerWrapper;
import it.bioko.utils.fields.Fields;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class CronService {
	
	private final Scheduler _scheduler;
	private final Context _context;

	public static void create(Context context) throws SystemException {
		try {
			if (context.get(Context.CRON) == null) {
				CronService cron = new CronService(context);
				context.put(Context.CRON, cron);
				context.addSystemListener(new SchedulerKiller(cron, context.getLogger()));
			}
		} catch (SchedulerException exception) {
			Logger logger = context.get(Context.LOGGER);
			logger.error("Creation of CRON service", exception);
			throw new SystemException(exception);
		}
	}
	
	private CronService(Context context) throws SchedulerException {
		SchedulerFactory factory = new StdSchedulerFactory();
		_scheduler = factory.getScheduler();
		_scheduler.start();
		_context = context;
	}
	
	public void register(Command command, Fields commandInput, String cronExpression, String notificationEmail) throws SystemException {
		register(command, commandInput, cronExpression, notificationEmail, null);
	}

	public void register(Command command, Fields commandInput, String cronExpression, String notificationEmail, ArrayList<CronJobListener> listeners) throws SystemException {
		if (listeners == null) {
			listeners = new ArrayList<CronJobListener>();
		}
		
		listeners.add(new CronEmailNotifier(notificationEmail));

		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(CommandRunnerJob.COMMAND_INPUT, commandInput);
		jobDataMap.put(CommandRunnerJob.COMMAND, command);
		
		JobDetail jobDetail = newJob(CommandRunnerJob.class).
				setJobData(jobDataMap).
				build();
		
		Trigger trigger = createTrigger(cronExpression);
				
		// job.getKey()
		
		try {
			_scheduler.getListenerManager().
				addJobListener(
						new QuartzCronListenerWrapper(listeners),
						keyEquals(jobDetail.getKey()));
					
			_scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException exception) {
			Logger logger = _context.get(Context.LOGGER);
			logger.error("Job registration", exception);
			throw new SystemException(exception);
		}
	}

	private Trigger createTrigger(String cronExpression) {
		return newTrigger().withSchedule(cronSchedule(cronExpression)).build();
	}
	
	public void stop() throws SystemException {
		try {
			_scheduler.shutdown();
		} catch (SchedulerException exception) {
			throw new SystemException(exception);
		}
	}
	
}
