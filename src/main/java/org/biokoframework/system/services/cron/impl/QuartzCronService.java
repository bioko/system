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

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.impl.matchers.KeyMatcher.keyEquals;

import java.util.ArrayList;
import java.util.List;

import org.biokoframework.system.KILL_ME.exception.SystemException;
import org.biokoframework.system.command.ICommand;
import org.biokoframework.system.services.cron.CronException;
import org.biokoframework.system.services.cron.ICronListener;
import org.biokoframework.system.services.cron.ICronService;
import org.biokoframework.system.services.email.IEmailService;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 12, 2014
 *
 */
@Singleton
public class QuartzCronService implements ICronService {

	static final String COMMAND = "command";
	static final String INJECTOR = "injector";
	
	private final Scheduler fScheduler;
	private final IEmailService fMailService;
	private final String fCronEmailAddress;
	private final Injector fInjector;
	
	@Inject
	public QuartzCronService(IEmailService mailService, @Named("cronEmailAddress") String cronEmailAddress, Injector injector) {
		try {
			fScheduler = StdSchedulerFactory.getDefaultScheduler();
			fScheduler.start();
		} catch (SchedulerException exception) {
			// TODO logger call
			throw new RuntimeException(exception);
		}
		fMailService = mailService;
		fCronEmailAddress = cronEmailAddress;
		fInjector = injector;
	}
	
	@Override
	public <C  extends ICommand> void schedule(Class<C> command, String cronExpression, String notificationEmail) throws CronException {
		schedule(command, cronExpression, notificationEmail, null);
	}

	@Override
	public <C extends ICommand> void schedule(Class<C> command, String cronExpression, String notificationEmail, List<ICronListener> listeners) throws CronException {
		try {
			JobDataMap jobData = new JobDataMap();
			jobData.put(COMMAND, command);
			jobData.put(INJECTOR, fInjector);
			
			JobDetail jobDetail = newJob(CommandJob.class).
					setJobData(jobData).
					build();
			
			Trigger jobTrigger = createCronTrigger(cronExpression);
			
			if (listeners == null) {
				listeners = new ArrayList<>(1);
			}
			listeners.add(new CronFailureNotifier(fMailService, fCronEmailAddress, notificationEmail));
			
			fScheduler.
				getListenerManager().
				addJobListener(new QuartzWrapperListener(listeners), 
						keyEquals(jobDetail.getKey()));
			
			fScheduler.scheduleJob(jobDetail, jobTrigger);
		} catch (SchedulerException exception) {
			// TODO logger call
			throw new CronException(exception);
		}
	}
	
	private Trigger createCronTrigger(String cronExpression) {
		return newTrigger().withSchedule(cronSchedule(cronExpression)).build();
	}

	@Override
	public void stop() throws SystemException {
		try {
			fScheduler.shutdown();
		} catch (SchedulerException exception) {
			throw new SystemException(exception);
		}
	}
	
}
