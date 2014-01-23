package it.bioko.system.context;

import it.bioko.system.command.AbstractCommandHandler;
import it.bioko.system.event.SystemListener;
import it.bioko.system.repository.core.Repository;
import it.bioko.utils.domain.DomainEntity;

import java.util.List;

import org.apache.log4j.Logger;

public abstract class Context {

	public static final String SYSTEM_NAME = "systemName";
	public static final String SYSTEM_VERSION = "systemVersion";
	public static final String SYSTEM_CONFIGURATION = "systemConfiguration";
	public static final String LOGGER = "logger";
	public static final String AUTHENTICATION_VALIDITY_INTERVAL_SECS = "authenticationValidityIntervalSecs";
	public static final String CRON = "cron";
	public static final String INPUT_VALIDATOR_RULES = "inputValidatorRules";
	public static final String COMMANDS_CLASS = "commandsClass";
	public static final String CUSTOM_COMMAND_VALIDATORS = "customCommandValidators";
	public static final String COMMANDS_FILTERS = "commandsFilters";
	
	public abstract <T> T get(String name);
	
	public abstract void put(String name, Object value);
	
	public abstract Logger getLogger() ;
	
	public abstract String getSystemName();
	
	public abstract void addRepository(String repoName, Repository<?> repo);
	
	
	public abstract <DE extends DomainEntity> Repository<DE> getRepository(String repoName);
	
	public abstract void setCommandHandler(AbstractCommandHandler commandHandler);
	
	public abstract AbstractCommandHandler getCommandHandler();
	
	public abstract void setSystemProperty(String name, String value);
	public abstract String getSystemProperty(String name);
	
	public abstract void addSystemListener(SystemListener listener);
	public abstract List<SystemListener> getSystemListeners();
}
