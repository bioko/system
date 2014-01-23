package it.bioko.system.service.cron.annotation;

import it.bioko.system.ConfigurationEnum;
import it.bioko.system.KILL_ME.XSystemIdentityCard;
import it.bioko.system.KILL_ME.exception.SystemException;
import it.bioko.system.command.AbstractCommandHandler;
import it.bioko.system.context.Context;
import it.bioko.system.service.cron.CronService;
import it.bioko.utils.fields.Fields;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;

public class AnnotatedSystemCronServiceInitializer {

	public static void initCronService(XSystemIdentityCard identityCard, Context context, Class<?> annotatedSystemCommands) throws IllegalArgumentException, IllegalAccessException, SystemException {
		
		Field[] classFields = annotatedSystemCommands.getFields();		
		AbstractCommandHandler commandHandler = context.getCommandHandler();

		boolean haveCronAnnotations = false;
		for(Field classField: classFields) {
			if (classField.getAnnotation(Cron.class)!=null) {
				haveCronAnnotations = true;
				break;
			}
		}
		
		if (!haveCronAnnotations)
			return;
		
		// else configure crons
		CronService.create(context);
		CronService cron = context.get(Context.CRON);
		
		for(Field classField: classFields) {
			Cron cronAnnotation = classField.getAnnotation(Cron.class);
			if (cronAnnotation!=null) {
				String commandName = classField.get(null).toString();
				String expression = _checkForCronExpression(identityCard.getSystemConfiguration(), cronAnnotation.expressions());
				if (expression!=null) {
					it.bioko.system.command.Command commandInstance = commandHandler.getByName(commandName);
					Fields inputFields = Fields.empty();
					if (!StringUtils.isEmpty(cronAnnotation.input()))
						inputFields.fromJson(cronAnnotation.input());

					cron.register(commandInstance, inputFields, expression, cronAnnotation.notifyTo());
				} else {
					context.getLogger().error("Unable to found cron expression for command: "+commandName+" with configuration: "+identityCard.getSystemConfiguration().name());
				}
			}
		}
		
	}

	private static String _checkForCronExpression(ConfigurationEnum systemConfiguration, CronExpression[] expressions) {
		String expressionString = null;
		
		for(CronExpression expression: expressions) {
			if (expression.conf().equals(systemConfiguration)) {
				expressionString = expression.exp();
				break;
			}
				
		}
		
		return expressionString;
	}

	
}



