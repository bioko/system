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

package org.biokoframework.system.factory;

import java.util.List;
import java.util.Map;

import org.biokoframework.system.KILL_ME.XSystem;
import org.biokoframework.system.KILL_ME.XSystemIdentityCard;
import org.biokoframework.system.KILL_ME.exception.SystemException;
import org.biokoframework.system.command.AbstractCommandHandler;
import org.biokoframework.system.command.AbstractFilter;
import org.biokoframework.system.command.ProxyCommandHandler;
import org.biokoframework.system.context.Context;
import org.biokoframework.system.context.ProxyContext;
import org.biokoframework.system.service.context.AbstractContextFactory;
import org.biokoframework.system.service.context.ContextFactory;
import org.biokoframework.system.service.cron.annotation.AnnotatedSystemCronServiceInitializer;
import org.biokoframework.system.service.validation.AbstractValidator;
import org.biokoframework.utils.validator.ValidatorRule;

public class AnnotatedSystemFactory {

	public static XSystem createSystem(XSystemIdentityCard identityCard, ContextFactory _systemContextFactory, Class<?> annotatedSystemCommands) throws IllegalArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException, SystemException {
		
		Context context = _systemContextFactory.create(identityCard);
		context.put(Context.COMMANDS_CLASS, annotatedSystemCommands);
		AbstractCommandHandler commandHandler = AnnotatedCommandHandlerFactory.create(annotatedSystemCommands, new ProxyContext(context), identityCard);		
		context.setCommandHandler(new ProxyCommandHandler(commandHandler));
		AnnotatedSystemCronServiceInitializer.initCronService(identityCard, context, annotatedSystemCommands);
		
		Map<String, Map<String,ValidatorRule>> inputValidatorRulesForCommands = AnnotatedSystemValidatorExtractor.extractInputValidators(annotatedSystemCommands);
		context.put(Context.INPUT_VALIDATOR_RULES, inputValidatorRulesForCommands);
		
		Map<String, List<AbstractValidator>> customCommandValidators = AnnotatedSystemValidatorExtractor.extractCustomValidatorClasses(annotatedSystemCommands, context);
		context.put(Context.CUSTOM_COMMAND_VALIDATORS, customCommandValidators);
		
		Map<String, List<AbstractFilter>> commandsFilters = AnnotatedSystemCommandFiltersExtractor.extractCommandFilters(annotatedSystemCommands, context);
		context.put(Context.COMMANDS_FILTERS, commandsFilters);
		
		
		for(String commandName: commandHandler.keys()) 
			commandHandler.getByName(commandName).onContextInitialized();

		return new XSystem(context);
	}

}
