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


package org.biokoframework.system.services.email;

import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.services.email.impl.EmailService;
import org.biokoframework.system.services.injection.ServiceModule;
import org.biokoframework.system.services.queue.IQueueService;

import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 12, 2014
 *
 */
public class EmailModule extends ServiceModule {

	public EmailModule(ConfigurationEnum config) {
		super(config);
	}
	
	@Override
	protected void configureForDev() {
		configureForDemo();
	}

	@Override
	protected void configureForDemo() {
		configureForProd();
	}

	@Override
	protected void configureForProd() {
		bind(IEmailService.class).to(EmailService.class);
		
		bind(IQueueService.class)
			.annotatedWith(Names.named("mailQueue"))
			.to(IQueueService.class)
			.in(Singleton.class);
	}

}
