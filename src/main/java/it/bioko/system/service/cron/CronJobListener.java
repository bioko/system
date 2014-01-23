package it.bioko.system.service.cron;

import it.bioko.utils.fields.Fields;


public abstract class CronJobListener {

	public void commandSuccessful(String commandName, Fields commandInput, Fields commandOutput) { };
	
	public void commandFailed(String commandName, Fields commandInput, Throwable cause) { };
	
}
