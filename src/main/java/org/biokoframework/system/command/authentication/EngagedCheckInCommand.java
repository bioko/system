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

package org.biokoframework.system.command.authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.context.Context;
import org.biokoframework.system.entity.authentication.Authentication;
import org.biokoframework.system.entity.authentication.AuthenticationManager;
import org.biokoframework.system.entity.description.ParameterEntity;
import org.biokoframework.system.entity.description.ParameterEntityBuilder;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.repository.core.SafeRepositoryHelper;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;


public class EngagedCheckInCommand extends AbstractCommand {

	@Override
	public Fields execute(Fields input) throws CommandException {
		logInput(input);
		Repository<Authentication> authenticationRepository = getRepository(Authentication.class);
		
		Fields result = new Fields();
		List<Map<String, Object>> response = new ArrayList<Map<String,Object>>();
		
//		AuthenticationStrategy authStrategy = AuthenticationStrategyFactory.retrieveCheckInStrategy(input);
//		
//		Login login = (Login) authStrategy.authenticate(fContext, input, false).get(Login.class.getSimpleName());
//		
//		Authentication authentication = insertNewAuthenticationFor(fContext, login, authenticationRepository);
//	
//		Map<String, Object> map = new HashMap<String, Object>();
//		
//		String token = authentication.get(GenericFieldNames.AUTH_TOKEN);
//		Long tokenExpire = authentication.get(GenericFieldNames.AUTH_TOKEN_EXPIRE);
//		
//		map.put(GenericFieldNames.AUTH_TOKEN, token);
//		map.put(GenericFieldNames.AUTH_TOKEN_EXPIRE, tokenExpire);
//		if (login.get(Login.ROLES) != null) {
//			map.put(Login.ROLES, login.get(Login.ROLES).toString());
//		}
//		response = Arrays.asList(map);
//		result.put(GenericFieldNames.TOKEN_HEADER, token);
//		result.put(GenericFieldNames.TOKEN_EXPIRE_HEADER, tokenExpire);
 		
		result.put(GenericFieldNames.RESPONSE, response);
		logOutput(result);
		return result;
	}

	private Authentication insertNewAuthenticationFor(Context context, Login login, Repository<Authentication> authenticationRepository) throws CommandException {
		Authentication newAuth = AuthenticationManager.createAuthenticationFor(context, login);
		newAuth = SafeRepositoryHelper.save(authenticationRepository, newAuth);
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
		
		return new Fields(GenericFieldNames.INPUT, parameters);
	}

	public Fields componingOutputKeys() {
		ArrayList<DomainEntity> parameters = new ArrayList<DomainEntity>();
		ParameterEntityBuilder builder = new ParameterEntityBuilder();
		builder.set(ParameterEntity.NAME, GenericFieldNames.AUTH_TOKEN);
		parameters.add(builder.build(false));
		builder.set(ParameterEntity.NAME, GenericFieldNames.AUTH_TOKEN_EXPIRE);
		parameters.add(builder.build(false));
				
		return new Fields(GenericFieldNames.OUTPUT, parameters);	
	}
	
}
