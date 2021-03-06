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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.entity.authentication.Authentication;
import org.biokoframework.system.entity.description.ParameterEntity;
import org.biokoframework.system.entity.description.ParameterEntityBuilder;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.services.authentication.token.ITokenAuthenticationService;
import org.biokoframework.system.services.authentication.token.TokenCreationException;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EngagedCheckInCommand extends AbstractCommand {

    private static final Logger LOGGER = Logger.getLogger(EngagedCheckInCommand.class);

    private ITokenAuthenticationService fAuthService;

	@Inject
	public EngagedCheckInCommand(ITokenAuthenticationService authService) {
		fAuthService = authService;
	}
	
	@Override
	public Fields execute(Fields input) throws CommandException {
		logInput(input);

        Repository<Login> loginRepo = getRepository(Login.class);
        Login login = loginRepo.retrieve((String) input.get("authLoginId"));

        Authentication auth = null;
        try {
            auth = fAuthService.requestToken(login);
        } catch (TokenCreationException exception) {
            LOGGER.error("Cannot create token", exception);
            throw CommandExceptionsFactory.createContainerException(exception);
        }

        String token = auth.get(Authentication.TOKEN);
        String tokenExpire = auth.get(Authentication.TOKEN_EXPIRE);
		Fields fields = new Fields(
				Authentication.TOKEN, token,
				Authentication.TOKEN_EXPIRE, tokenExpire);

		String roles = auth.get(Authentication.ROLES);
		if (!StringUtils.isEmpty(roles)) {
			fields.put(Authentication.ROLES, roles);
		}
		
		List<Fields> response = Arrays.asList(fields);
 		
		Fields result = new Fields(GenericFieldNames.RESPONSE, response,
                Authentication.TOKEN, token,
                Authentication.TOKEN_EXPIRE, tokenExpire);
		logOutput(result);
		return result;
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
