/*
 * Copyright (c) 2014																 
 *	Mikol Faro			<mikol.faro@gmail.com>
 *	Simone Mangano		<simone.mangano@ieee.org>
 *	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

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
