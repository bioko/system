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

package it.bioko.system.command.authentication;

import it.bioko.system.KILL_ME.commons.GenericCommandNames;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericRepositoryNames;
import it.bioko.system.KILL_ME.commons.logger.Loggers;
import it.bioko.system.command.Command;
import it.bioko.system.command.CommandException;
import it.bioko.system.context.Context;
import it.bioko.system.entity.authentication.Authentication;
import it.bioko.system.entity.authentication.AuthenticationManager;
import it.bioko.system.entity.description.ParameterEntity;
import it.bioko.system.entity.description.ParameterEntityBuilder;
import it.bioko.system.entity.login.Login;
import it.bioko.system.repository.core.Repository;
import it.bioko.system.repository.core.SafeRepositoryHelper;
import it.bioko.system.service.authentication.strategies.AuthenticationStrategy;
import it.bioko.system.service.authentication.strategies.AuthenticationStrategyFactory;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.fields.Fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


public class EngagedCheckInCommand extends Command {

	private Repository<Authentication> _authenticationRepository;

	@Override
	public void onContextInitialized() {
		_authenticationRepository = _context.getRepository(GenericRepositoryNames.AUTHENTICATION_REPOSITORY);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Fields execute(Fields input) throws CommandException {
		Logger logger = _context.get(Context.LOGGER);
		logger.info("EXECUTING Command:" + this.getClass().getSimpleName());
		logger.info("INPUT: " + input.asString());
		
		Fields result = Fields.empty();
		List<Map<String, String>> response = new ArrayList<Map<String,String>>();
		
		AuthenticationStrategy authStrategy = AuthenticationStrategyFactory.retrieveCheckInStrategy(input);
		
		Login login = (Login) authStrategy.authenticate(_context, input, false).objectNamed(Login.class.getSimpleName());
		
		Authentication authentication = insertNewAuthenticationFor(_context, login);
	
		Map<String, String> map = new HashMap<String, String>();
		
		String token = authentication.get(GenericFieldNames.AUTH_TOKEN);
		String tokenExpire = authentication.get(GenericFieldNames.AUTH_TOKEN_EXPIRE);
		
		map.put(GenericFieldNames.AUTH_TOKEN, token);
		map.put(GenericFieldNames.AUTH_TOKEN_EXPIRE, tokenExpire);
		if (!StringUtils.isEmpty(login.get(Login.ROLES))) {
			map.put(Login.ROLES, login.get(Login.ROLES));
		}
		response = Arrays.asList(map);
		result.put(GenericFieldNames.TOKEN_HEADER, token);
		result.put(GenericFieldNames.TOKEN_EXPIRE_HEADER, tokenExpire);
 		
		result.put(GenericFieldNames.RESPONSE, response);
		Loggers.xsystem.info("OUTPUT after execution: " + result.asString());
		Loggers.xsystem.info("END Command:" + this.getClass().getSimpleName());
		return result;
	}

	private Authentication insertNewAuthenticationFor(Context context, Login login) throws CommandException {
		Authentication newAuth = AuthenticationManager.createAuthenticationFor(context, login);
		newAuth = SafeRepositoryHelper.save(_authenticationRepository, newAuth, _context);
		return newAuth;
	}

	public Fields componingInputKeys() {
		ArrayList<DomainEntity> parameters = new ArrayList<DomainEntity>();
		
		ParameterEntityBuilder builder = new ParameterEntityBuilder();
		builder.set(ParameterEntity.NAME, GenericFieldNames.USER_EMAIL);
		parameters.add(builder.build(false));
		builder.set(ParameterEntity.NAME, GenericFieldNames.PASSWORD);
		parameters.add(builder.build(false));
		builder.set(ParameterEntity.NAME, GenericFieldNames.FACEBOOK_TOKEN);
		parameters.add(builder.build(false));
		
		Fields resultFields = Fields.single(GenericFieldNames.INPUT, parameters);
		return resultFields;
	}

	public Fields componingOutputKeys() {
		ArrayList<DomainEntity> parameters = new ArrayList<DomainEntity>();
		ParameterEntityBuilder builder = new ParameterEntityBuilder();
		builder.set(ParameterEntity.NAME, GenericFieldNames.AUTH_TOKEN);
		parameters.add(builder.build(false));
		builder.set(ParameterEntity.NAME, GenericFieldNames.AUTH_TOKEN_EXPIRE);
		parameters.add(builder.build(false));
		
		Fields fields = Fields.empty();		
		fields.put(GenericFieldNames.OUTPUT, parameters);
		return fields;	
	}
	
	@Override
	public String getName() {
		return GenericCommandNames.ENGAGED_CHECK_IN;
	}

}
