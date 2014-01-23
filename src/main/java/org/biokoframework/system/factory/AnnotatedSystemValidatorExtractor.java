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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.biokoframework.system.KILL_ME.commons.HttpMethod;
import org.biokoframework.system.command.annotation.Command;
import org.biokoframework.system.command.annotation.CommandInputs;
import org.biokoframework.system.command.crud.annotation.CrudCommand;
import org.biokoframework.system.context.Context;
import org.biokoframework.system.service.validation.AbstractValidator;
import org.biokoframework.system.service.validation.Validators;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.annotation.field.EntityValidatorRulesFactory;
import org.biokoframework.utils.validator.ValidatorRule;
import org.biokoframework.utils.validator.ValidatorRuleFactory;

public class AnnotatedSystemValidatorExtractor {

	public static Map<String, Map<String,ValidatorRule>> extractInputValidators(Class<?> annotatedSystemCommands) throws IllegalArgumentException, IllegalAccessException {
		Map<String, Map<String,ValidatorRule>> commandInputValidations = new HashMap<String, Map<String,ValidatorRule>>();

		Field[] classFields = annotatedSystemCommands.getFields();

		for (Field classField: classFields) {	// for every command defined
			String baseCommandName = classField.get(null).toString();

			_seachCommandInputValidators(classField, baseCommandName, commandInputValidations);
			_seachCrudCommandInputValidators(classField, baseCommandName, commandInputValidations);
		}

		return commandInputValidations;

	}
	
	public static Map<String, List<AbstractValidator>> extractCustomValidatorClasses(Class<?> annotatedSystemCommands, Context context) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Map<String, List<AbstractValidator> > customValidatorClasses = new HashMap<String, List<AbstractValidator>>();
		
		Field[] classFields = annotatedSystemCommands.getFields();

		for (Field classField: classFields) {	// for every command defined
			String baseCommandName = classField.get(null).toString();
			
			_searchCommandSpecificValdators(classField, baseCommandName, context, customValidatorClasses );
			_searchCrudCommandSpecificValdators(classField, baseCommandName, context, customValidatorClasses );
			
		}
		
		return customValidatorClasses;
		
	}
	
	

	private static void _searchCommandSpecificValdators(Field classField, String baseCommandName, Context context,
			Map<String, List<AbstractValidator>> customValidators) throws InstantiationException, IllegalAccessException {
		
		Command commandAnnotation = classField.getAnnotation(Command.class);
		Validators validatorAnnotation = classField.getAnnotation(Validators.class);
		if (validatorAnnotation!=null && commandAnnotation!=null) {
			List<AbstractValidator> customValidatorsForCommand = new ArrayList<AbstractValidator>();
			
			
			Class<? extends AbstractValidator> customValidatorClasses[] = validatorAnnotation.value();
			for(Class<? extends AbstractValidator> customValidatorClass: customValidatorClasses) {
				AbstractValidator customValidator = customValidatorClass.newInstance();
				customValidator.setContext(context);
				customValidator.onContextInitialized();
				customValidatorsForCommand.add(customValidator);
			}
			
			String commandPrefix = "";
			if (!commandAnnotation.rest().equals(HttpMethod.NONE)) {
				commandPrefix = commandAnnotation.rest().name() + "_";
			}
			
			customValidators.put(commandPrefix+baseCommandName, customValidatorsForCommand);		
		}
		
	}
	
	
	private static void _searchCrudCommandSpecificValdators(Field classField, String baseCommandName, Context context,
			Map<String, List<AbstractValidator>> customValidators) throws InstantiationException, IllegalAccessException {
				
		CrudCommand crudCommandAnnotation = classField.getAnnotation(CrudCommand.class);
				
		Validators validatorAnnotation = classField.getAnnotation(Validators.class);
		if (validatorAnnotation!=null && crudCommandAnnotation!=null) {
			List<AbstractValidator> customValidatorsForCommand = new ArrayList<AbstractValidator>();
			
			
			Class<? extends AbstractValidator> customValidatorClasses[] = validatorAnnotation.value();
			for(Class<? extends AbstractValidator> customValidatorClass: customValidatorClasses) {
				AbstractValidator customValidator = customValidatorClass.newInstance();
				customValidator.setContext(context);
				customValidator.onContextInitialized();
				customValidatorsForCommand.add(customValidator);
			}
			
			if (crudCommandAnnotation.create())
				customValidators.put(HttpMethod.POST.name()+"_"+baseCommandName, customValidatorsForCommand);
			if (crudCommandAnnotation.update())
				customValidators.put(HttpMethod.PUT.name()+"_"+baseCommandName, customValidatorsForCommand);
			if (crudCommandAnnotation.delete())
				customValidators.put(HttpMethod.DELETE.name()+"_"+baseCommandName, customValidatorsForCommand);
			if (crudCommandAnnotation.read())
				customValidators.put(HttpMethod.GET.name()+"_"+baseCommandName, customValidatorsForCommand);
			if (crudCommandAnnotation.head())
				customValidators.put(HttpMethod.HEAD.name()+"_"+baseCommandName, customValidatorsForCommand);
		}
		
	}
	
	
	
	

	private static void _seachCrudCommandInputValidators(Field classField, String baseCommandName,
			Map<String, Map<String, ValidatorRule>> commandInputValidations) throws IllegalArgumentException, IllegalAccessException {
		
		CrudCommand crudCommandAnnotation = classField.getAnnotation(CrudCommand.class);
		if (crudCommandAnnotation!=null) {
			Map<String, ValidatorRule> domainEntityValidatorRules = EntityValidatorRulesFactory.create(crudCommandAnnotation.entity());
			if (domainEntityValidatorRules!=null) {
				if (crudCommandAnnotation.create()) {
					commandInputValidations.put(HttpMethod.POST.name()+"_"+baseCommandName, domainEntityValidatorRules);
				}
				if (crudCommandAnnotation.update()) {
					Map<String, ValidatorRule> domainEntityValidatorRulesWithId = new HashMap<String, ValidatorRule>(domainEntityValidatorRules);
					domainEntityValidatorRulesWithId.put(DomainEntity.ID, new ValidatorRule(String.class, true, null, null));
					commandInputValidations.put(HttpMethod.PUT.name()+"_"+baseCommandName, domainEntityValidatorRulesWithId);
				}				
			}
			CommandInputs commandInputsAnnotation = classField.getAnnotation(CommandInputs.class);
			if (commandInputsAnnotation!=null) {
				Map<String, ValidatorRule> commandInputValidatorRules = getCommandInputValidationRules(commandInputsAnnotation);
				if (crudCommandAnnotation.head()) 
					commandInputValidations.put(HttpMethod.HEAD.name()+"_"+baseCommandName, commandInputValidatorRules);
				if (crudCommandAnnotation.read()) 
					commandInputValidations.put(HttpMethod.GET.name()+"_"+baseCommandName, commandInputValidatorRules);
				if (crudCommandAnnotation.delete()) 
					commandInputValidations.put(HttpMethod.DELETE.name()+"_"+baseCommandName, commandInputValidatorRules);
				
			}
				

		}
		
	}

	private static void _seachCommandInputValidators(Field classField, String baseCommandName,
			Map<String, Map<String, ValidatorRule>> commandInputValidations) {

		Command commandAnnotation = classField.getAnnotation(Command.class);
		CommandInputs commandInputsAnnotation = classField.getAnnotation(CommandInputs.class);

		if (commandAnnotation!=null && commandInputsAnnotation!=null) {
			Map<String, ValidatorRule> validatorRulesForCommand = getCommandInputValidationRules(commandInputsAnnotation);
			String commandPrefix = "";
			if (!commandAnnotation.rest().equals(HttpMethod.NONE)) {
				commandPrefix = commandAnnotation.rest().name() + "_";
			}
			
			commandInputValidations.put(commandPrefix+baseCommandName, validatorRulesForCommand);

		}


	}

	private static Map<String, ValidatorRule> getCommandInputValidationRules(CommandInputs commandInputsAnnotation) {
		Map<String, ValidatorRule> validatorRulesForCommand = new HashMap<String, ValidatorRule>();
		org.biokoframework.utils.domain.annotation.field.Field[] fieldAnnotations = commandInputsAnnotation.value();
		for (org.biokoframework.utils.domain.annotation.field.Field fieldAnnotation: fieldAnnotations) {
			String fieldName = fieldAnnotation.name();
			ValidatorRule rule = ValidatorRuleFactory.fromAnnotation(fieldName, fieldAnnotation);
			validatorRulesForCommand.put(fieldName, rule);					
		}
		return validatorRulesForCommand;
	}

}
