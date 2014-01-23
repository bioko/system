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
