package it.bioko.system.factory;

import it.bioko.system.KILL_ME.XSystemIdentityCard;
import it.bioko.system.KILL_ME.commons.logger.Loggers;
import it.bioko.system.context.Context;
import it.bioko.system.context.ContextImpl;
import it.bioko.utils.fields.Fields;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.log4j.Logger;

// Use AnnotatedSystemFactory instead
@Deprecated
public abstract class AbstractSystemFactory {

	protected Context createContext(XSystemIdentityCard systemCard) {
		Fields contextFields = Fields.empty();
		contextFields.put(Context.SYSTEM_NAME, systemCard.getSystemName());
		contextFields.put(Context.SYSTEM_VERSION, systemCard.getSystemVersion());
		contextFields.put(Context.SYSTEM_CONFIGURATION, systemCard.getSystemConfiguration());
		
		Logger logger = Logger.getLogger(systemCard.getSystemName());
		if (logger == null) {
			logger = Loggers.xsystem;
		}
		contextFields.put(Context.LOGGER, logger);
		
		Context context = new ContextImpl(contextFields);
		loadProperties(context);
		
		return context;
	}

	private void loadProperties(Context context) {
		try {
			String fileName = new StringBuilder().
					append(context.getSystemName()).
					append(".").append(context.get(Context.SYSTEM_CONFIGURATION)).
					append(".properties").toString();

			InputStream propertiesFileStream = new AutoCloseInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
			if (propertiesFileStream != null) {
			
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
	
}
