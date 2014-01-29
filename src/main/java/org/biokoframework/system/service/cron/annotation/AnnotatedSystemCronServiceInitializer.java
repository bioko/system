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

package org.biokoframework.system.service.cron.annotation;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.KILL_ME.XSystemIdentityCard;
import org.biokoframework.system.KILL_ME.exception.SystemException;
import org.biokoframework.system.command.AbstractCommandHandler;
import org.biokoframework.system.context.Context;
import org.biokoframework.system.service.cron.CronService;
import org.biokoframework.utils.fields.Fields;

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
					org.biokoframework.system.command.Command commandInstance = commandHandler.getByName(commandName);
					Fields inputFields = new Fields();
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



