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

package org.biokoframework.system.services.cron.impl;

import java.util.List;

import org.biokoframework.system.command.ICommand;
import org.biokoframework.system.services.cron.ICronListener;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 13, 2014
 *
 */
public class QuartzWrapperListener extends JobListenerSupport {

	private final List<ICronListener> fActualListeners;

	public QuartzWrapperListener(List<ICronListener> listeners) {
		fActualListeners = listeners;
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		JobDataMap jobData = context.getJobDetail().getJobDataMap();
		
		Class<? extends ICommand> command = (Class<? extends ICommand>) jobData.get(QuartzCronService.COMMAND);
		
		if (jobException == null) {
			for (ICronListener aListener : fActualListeners) {
				aListener.commandFinished(command);
			}
		} else {
			for (ICronListener aListener : fActualListeners) {
				aListener.commandFailed(command, jobException.getCause());
			}
		}
	}

}
