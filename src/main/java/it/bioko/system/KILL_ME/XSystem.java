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

package it.bioko.system.KILL_ME;

import it.bioko.system.KILL_ME.exception.CommandNotFoundException;
import it.bioko.system.KILL_ME.exception.SystemException;
import it.bioko.system.command.AbstractCommandHandler;
import it.bioko.system.command.AbstractFilter;
import it.bioko.system.command.Command;
import it.bioko.system.command.CommandException;
import it.bioko.system.command.ValidationException;
import it.bioko.system.context.Context;
import it.bioko.system.event.SystemListener;
import it.bioko.system.service.validation.AbstractValidator;
import it.bioko.utils.domain.ErrorEntity;
import it.bioko.utils.fields.FieldNames;
import it.bioko.utils.fields.Fields;
import it.bioko.utils.validator.Validator;
import it.bioko.utils.validator.ValidatorRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONValue;


public class XSystem {

	private AbstractCommandHandler _commandHandler;
	private final Logger _logger;
	private Context _context;
	private Map<String, Map<String, ValidatorRule>> _inputValidatorRules;
	private Map<String, List<AbstractValidator>> _customCommandValidators;
	private Map<String, List<AbstractFilter>> _commandsFilters;

//	public XSystem(AbstractCommandHandler commandHandler, Logger logger) {
//		_commandHandler = commandHandler;
//		_logger = logger;
//	}
	
	

	@SuppressWarnings("unchecked")
	public XSystem(Context context) {
		_commandHandler = context.getCommandHandler();
		_logger = context.getLogger();
		_context = context;
		
		_inputValidatorRules = (Map<String, Map<String, ValidatorRule>>)  _context.get(Context.INPUT_VALIDATOR_RULES);
		_customCommandValidators = (Map<String, List<AbstractValidator>>) _context.get(Context.CUSTOM_COMMAND_VALIDATORS);
		_commandsFilters = (Map<String, List<AbstractFilter>> ) _context.get(Context.COMMANDS_FILTERS);
		if (_commandsFilters==null)
			_commandsFilters = new HashMap<String, List<AbstractFilter>>();
	}

	public Fields execute(Fields input) throws SystemException {
		Fields output = Fields.empty();
		String commandName = (String)input.stringNamed(FieldNames.COMMAND_NAME);
		try {
			_logger.info("----- Executing Command: " + commandName + " -----");
			_logger.info("Command input: " + input.asString());
			Command command = _commandHandler.getByName(commandName);
			if (command == null) {
				Fields fields = Fields.single(ErrorEntity.ERROR_MESSAGE, "Command " + commandName + " not found.");
				fields.put(ErrorEntity.ERROR_CODE, "101");
				throw new CommandNotFoundException(new ErrorEntity(fields));
			}
			
			List<ErrorEntity> validationErrors = null;
			if (_inputValidatorRules!= null && _inputValidatorRules.containsKey(commandName)) {
				_logger.info("Validating inputs: ");
				Map<String, ValidatorRule> commandValidatorRules = _inputValidatorRules.get(commandName);
				Validator commandValidator = new Validator(commandValidatorRules);
				boolean valid = commandValidator.validate(input);
				_logger.info("  -> result: "+valid);
				if (!valid) {
					_logger.info("  -> validation errors: "+JSONValue.toJSONString(commandValidator.getErrors()));
					validationErrors = new ArrayList<ErrorEntity>(commandValidator.getErrors());
//					throw new ValidationException(commandValidator.getErrors());
				}
			}
			
			if (_customCommandValidators!=null && _customCommandValidators.containsKey(commandName)) {
				_logger.info("Running custom validators");
				if (validationErrors==null)
					validationErrors = new ArrayList<ErrorEntity>();
				
				List<AbstractValidator> customValidatorsForCommand = _customCommandValidators.get(commandName);
				for(AbstractValidator validator: customValidatorsForCommand) {
					validator.validate(input, validationErrors);
				}
			}
			
			if (validationErrors!=null && !validationErrors.isEmpty())
				throw new ValidationException(validationErrors);
			
			
			List<AbstractFilter> thisCommandFilters = _commandsFilters.get(commandName);
			// execute input filters
			if (thisCommandFilters!=null && !thisCommandFilters.isEmpty()) {
				for(AbstractFilter filter: thisCommandFilters)
					filter.filterInput(input);
			}
			
			output = command.execute(input);
			
			// execute output filters
			if (thisCommandFilters!=null && !thisCommandFilters.isEmpty()) {
				for(AbstractFilter filter: thisCommandFilters)
					filter.filterOutput(output);
			}
			
			
		} catch (SystemException systemException) {
			_logger.error("System exception",systemException);
			throw systemException;
		} catch (Exception exception) {
			_logger.error("Generic exception",exception);
			throw new CommandException(exception);
		}
		_logger.info("Command output: " + output.asString());
		_logger.info("----- Command execution finished -----");
		return output;
	}

	public void shutdown() {
		_logger.info("System: " + _context.getSystemName() + " is going down");
		_logger.info("Bye! Bye!");
		List<SystemListener> listeners = _context.getSystemListeners();
		for (SystemListener aListener : listeners) {
			aListener.systemShutdown();
		}
	}
}