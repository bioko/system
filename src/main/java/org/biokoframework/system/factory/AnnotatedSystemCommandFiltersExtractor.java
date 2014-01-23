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

import org.apache.commons.lang3.ArrayUtils;
import org.biokoframework.system.KILL_ME.commons.HttpMethod;
import org.biokoframework.system.command.AbstractFilter;
import org.biokoframework.system.command.annotation.Command;
import org.biokoframework.system.command.annotation.CommandFilters;
import org.biokoframework.system.command.crud.annotation.CrudCommand;
import org.biokoframework.system.context.Context;
import org.biokoframework.system.service.authentication.AuthFilter;
import org.biokoframework.system.service.authentication.annotation.Auth;

public class AnnotatedSystemCommandFiltersExtractor {

	public static Map<String, List<AbstractFilter>> extractCommandFilters(Class<?> annotatedSystemCommands, Context context) 
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Map<String, List<AbstractFilter> > allCommandFilters = new HashMap<String, List<AbstractFilter>>();

		Field[] classFields = annotatedSystemCommands.getFields();

		for (Field classField: classFields) {	// for every command defined
			String baseCommandName = classField.get(null).toString();

			_searchFilters(classField, baseCommandName, context, allCommandFilters );
		}

		return allCommandFilters;
	}

	private static void _searchFilters(Field classField, String baseCommandName, Context context,
			Map<String, List<AbstractFilter>> allCommandFilters) throws InstantiationException, IllegalAccessException {
		
		CrudCommand crudCommandAnnotation = classField.getAnnotation(CrudCommand.class);
		Command commandAnnotation = classField.getAnnotation(Command.class);
		Auth authAnnotation = classField.getAnnotation(Auth.class);
		CommandFilters filtersAnnotation = classField.getAnnotation(CommandFilters.class);
		
		if ((commandAnnotation != null && !ArrayUtils.contains(commandAnnotation.hideOn(), context.get(Context.SYSTEM_CONFIGURATION))) || 
				(crudCommandAnnotation != null && !ArrayUtils.contains(crudCommandAnnotation.hideOn(), context.get(Context.SYSTEM_CONFIGURATION)))) {
			
			ArrayList<AbstractFilter> thisCommandFilters = new ArrayList<AbstractFilter>();
			// auth filter is executed by default
			boolean checkAuth = false;
			String[] roles = null;
			if (authAnnotation!=null) {
				checkAuth=true;
				roles = authAnnotation.roles();
			}
//			TokenAuthFilter authFilter = new TokenAuthFilter(checkAuth, roles);
//			BasicAuthFilter authFilter = new BasicAuthFilter(checkAuth, roles);
			AuthFilter authFilter = new AuthFilter(checkAuth, roles);
			
			authFilter.setContext(context);
			authFilter.onContextInitialized();
			thisCommandFilters.add(authFilter);
			
			if (filtersAnnotation!=null) {
				Class<? extends AbstractFilter>[] filterClasses = filtersAnnotation.value();
				for(Class<? extends AbstractFilter> filterClass: filterClasses) {
					AbstractFilter aFilter = filterClass.newInstance();
					aFilter.setContext(context);
					aFilter.onContextInitialized();
					thisCommandFilters.add(aFilter);
				}
			}
			
			
			if (commandAnnotation!=null && !thisCommandFilters.isEmpty()) {
				String commandPrefix = "";
				if (!commandAnnotation.rest().equals(HttpMethod.NONE))
					commandPrefix = commandAnnotation.rest().name()+"_";
					
				allCommandFilters.put(commandPrefix+baseCommandName, thisCommandFilters);
				
			} else if (crudCommandAnnotation!=null && !thisCommandFilters.isEmpty()) {
				if (crudCommandAnnotation.create())
					allCommandFilters.put(HttpMethod.POST.name()+"_"+baseCommandName, thisCommandFilters);
				if (crudCommandAnnotation.update())
					allCommandFilters.put(HttpMethod.PUT.name()+"_"+baseCommandName, thisCommandFilters);
				if (crudCommandAnnotation.delete())
					allCommandFilters.put(HttpMethod.DELETE.name()+"_"+baseCommandName, thisCommandFilters);
				if (crudCommandAnnotation.read())
					allCommandFilters.put(HttpMethod.GET.name()+"_"+baseCommandName, thisCommandFilters);
				if (crudCommandAnnotation.head())
					allCommandFilters.put(HttpMethod.HEAD.name()+"_"+baseCommandName, thisCommandFilters);
			}
			
			
		}
		
	}

}
