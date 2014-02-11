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

package org.biokoframework.system.service.context;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.log4j.Logger;
import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.KILL_ME.XSystemIdentityCard;
import org.biokoframework.system.KILL_ME.commons.GenericConstants;
import org.biokoframework.system.KILL_ME.commons.GenericFieldValues;
import org.biokoframework.system.context.Context;
import org.biokoframework.system.context.ContextImpl;
import org.biokoframework.system.service.queue.Queue;
import org.biokoframework.system.service.queue.QueuedItem;
import org.biokoframework.system.services.random.impl.TestRandomGeneratorService;
import org.biokoframework.utils.exception.BiokoException;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;

public abstract class AbstractContextFactory implements ContextFactory {

	public Context create(XSystemIdentityCard identityCard) throws BiokoException {


		Fields identityFields = new Fields();
		identityFields.put(Context.SYSTEM_CONFIGURATION, identityCard.getSystemConfiguration());
		identityFields.put(Context.SYSTEM_NAME, identityCard.getSystemName());
		identityFields.put(Context.SYSTEM_VERSION, identityCard.getSystemVersion());
		
		Context context = new ContextImpl(identityFields);
		context.put(Context.LOGGER, getSystemLogger());
		
		addDefaultProperties(context);
		loadProperties(context);
		
		addPasswordGenerator(context, identityCard.getSystemConfiguration());

		switch (identityCard.getSystemConfiguration()) {
		case PROD:
			context = configureForProd(context);
			break;
			
		case DEV:
			context = configureForDev(context);
			break;
		
		case DEMO:
			context = configureForDemo(context);
			break;
			
		default:
			return null;
		}
		
		return context;
	}




	protected abstract Context configureForProd(Context context) throws BiokoException;
	
	protected abstract Context configureForDev(Context context) throws BiokoException;
	
	protected abstract Context configureForDemo(Context context) throws BiokoException;
	
	protected abstract Logger getSystemLogger();

	protected void addDefaultProperties(Context context) {
		context.setSystemProperty(Context.AUTHENTICATION_VALIDITY_INTERVAL_SECS, Long.toString(GenericFieldValues.DEFAULT_AUTHENTICATION_VALIDITY_INTERVAL_SECS));
	}
	
	protected void loadProperties(Context context) {
		try {
			String fileName = new StringBuilder().
					append(context.getSystemName()).
					append(".").append(context.get(Context.SYSTEM_CONFIGURATION)).
					append(".properties").toString();

			InputStream propertiesFileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
			if (propertiesFileStream != null) {
				propertiesFileStream = new AutoCloseInputStream(propertiesFileStream);
				
				Properties properties = new Properties();
				properties.load(propertiesFileStream);
				
				for (String aProperty : properties.stringPropertyNames()) {
					context.setSystemProperty(aProperty, properties.getProperty(aProperty));
				}
			}
			
		} catch (Exception exception) {
			context.getLogger().error("Unable to load properties file", exception);
		}
	}
	
	protected void addPasswordGenerator(Context context, ConfigurationEnum configurationEnum) {
		context.put(GenericConstants.RANDOM_PASSWORD_GENERATOR, new TestRandomGeneratorService());
	}
	
	protected void addQueue(Context context, String queueName, String queueRepositoryName) {
		Repository<QueuedItem> baseRepository = context.getRepository(queueRepositoryName);
		context.put(queueName, new Queue(baseRepository));
	}
	
}
