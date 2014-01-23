package it.bioko.system.service.cron.quartz;

import it.bioko.system.KILL_ME.commons.logger.Loggers;
import it.bioko.system.command.Command;
import it.bioko.utils.fields.Fields;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class CommandRunnerJob implements Job {

	public static final String COMMAND = "command";
	public static final String COMMAND_INPUT = "commandInput";
	public static final String COMMAND_OUTPUT = "commandOutput";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data = context.getJobDetail().getJobDataMap();
		
		try {
			Command command = (Command) data.get(COMMAND);
			Fields input = (Fields) data.get(COMMAND_INPUT);
		
			Fields output = command.execute(input);
			
			data.put(COMMAND_OUTPUT, output);
		
		} catch (Exception exception) {
			Loggers.jobs.error("Job execution", exception);
			
			throw new JobExecutionException(exception);
		}
	}
}
