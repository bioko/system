package it.bioko.system.factory;

import it.bioko.system.KILL_ME.commons.HttpMethod;
import it.bioko.system.command.annotation.Command;
import it.bioko.system.command.annotation.CommandInputs;
import it.bioko.system.command.crud.annotation.CrudCommand;
import it.bioko.system.context.Context;
import it.bioko.system.service.validation.AbstractValidator;
import it.bioko.system.service.validation.Validators;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.EntityValidatorRulesFactory;
import it.bioko.utils.validator.ValidatorRule;
import it.bioko.utils.validator.ValidatorRuleFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		it.bioko.utils.domain.annotation.field.Field[] fieldAnnotations = commandInputsAnnotation.value();
		for (it.bioko.utils.domain.annotation.field.Field fieldAnnotation: fieldAnnotations) {
			String fieldName = fieldAnnotation.name();
			ValidatorRule rule = ValidatorRuleFactory.fromAnnotation(fieldName, fieldAnnotation);
			validatorRulesForCommand.put(fieldName, rule);					
		}
		return validatorRulesForCommand;
	}

}
