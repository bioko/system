package it.bioko.system.factory;

import it.bioko.system.KILL_ME.commons.HttpMethod;
import it.bioko.system.command.AbstractFilter;
import it.bioko.system.command.annotation.Command;
import it.bioko.system.command.annotation.CommandFilters;
import it.bioko.system.command.crud.annotation.CrudCommand;
import it.bioko.system.context.Context;
import it.bioko.system.service.authentication.AuthFilter;
import it.bioko.system.service.authentication.annotation.Auth;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

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
