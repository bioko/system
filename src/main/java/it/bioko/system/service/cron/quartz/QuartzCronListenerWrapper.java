package it.bioko.system.service.cron.quartz;

import it.bioko.system.command.Command;
import it.bioko.system.service.cron.CronJobListener;
import it.bioko.utils.fields.Fields;

import java.util.ArrayList;
import java.util.UUID;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.listeners.JobListenerSupport;

public class QuartzCronListenerWrapper extends JobListenerSupport implements JobListener {

	private final ArrayList<CronJobListener> _actualListeners;
	private final String _name;

	public QuartzCronListenerWrapper(ArrayList<CronJobListener> listeners) {
		_actualListeners = listeners;
		_name = UUID.randomUUID().toString();
	}

	@Override
	public String getName() {
		return _name;
	}
	
	@Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		JobDataMap jobData = context.getJobDetail().getJobDataMap();
		
		Command command = (Command) jobData.get(CommandRunnerJob.COMMAND);
		//String commandName = command.getClass().getSimpleName();
		String commandName = command.getName();
		
		Fields commandInput = (Fields) jobData.get(CommandRunnerJob.COMMAND_INPUT);
		Fields commandOutput = (Fields) jobData.get(CommandRunnerJob.COMMAND_OUTPUT);

		if (jobException == null) {
			for (CronJobListener aListener : _actualListeners) {
				aListener.commandSuccessful(commandName, commandInput, commandOutput);
			}
		} else {
			for (CronJobListener aListener : _actualListeners) {
				aListener.commandFailed(commandName, commandInput, jobException.getCause());
			}
		}
    }

}
