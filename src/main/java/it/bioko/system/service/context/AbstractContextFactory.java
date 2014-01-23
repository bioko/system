package it.bioko.system.service.context;

import it.bioko.system.ConfigurationEnum;
import it.bioko.system.KILL_ME.XSystemIdentityCard;
import it.bioko.system.KILL_ME.commons.GenericConstants;
import it.bioko.system.KILL_ME.commons.GenericFieldValues;
import it.bioko.system.KILL_ME.exception.SystemException;
import it.bioko.system.context.Context;
import it.bioko.system.context.ContextImpl;
import it.bioko.system.repository.core.Repository;
import it.bioko.system.service.queue.Queue;
import it.bioko.system.service.queue.QueuedItem;
import it.bioko.system.service.random.RandomGeneratorService;
import it.bioko.utils.fields.Fields;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.log4j.Logger;

public abstract class AbstractContextFactory implements ContextFactory {

	public Context create(XSystemIdentityCard identityCard) throws SystemException {


		Fields identityFields = Fields.empty();
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




	protected abstract Context configureForProd(Context context) throws SystemException;
	
	protected abstract Context configureForDev(Context context) throws SystemException;
	
	protected abstract Context configureForDemo(Context context) throws SystemException;
	
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
		context.put(GenericConstants.RANDOM_PASSWORD_GENERATOR, new RandomGeneratorService(configurationEnum));
	}
	
	protected void addQueue(Context context, String queueName, String queueRepositoryName) {
		Repository<QueuedItem> baseRepository = context.getRepository(queueRepositoryName);
		context.put(queueName, new Queue(baseRepository));
	}
	
}
