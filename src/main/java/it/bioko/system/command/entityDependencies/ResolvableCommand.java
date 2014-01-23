package it.bioko.system.command.entityDependencies;

import it.bioko.system.KILL_ME.commons.GenericCommandNames;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericFieldValues;
import it.bioko.system.KILL_ME.commons.logger.Loggers;
import it.bioko.system.command.Command;
import it.bioko.system.command.CommandException;
import it.bioko.system.entity.description.ParameterEntity;
import it.bioko.system.entity.resolution.AnnotatedEntityResolver;
import it.bioko.system.entity.resolution.EntityResolver;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.system.repository.core.Repository;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.fields.Fields;

import java.util.ArrayList;

public class ResolvableCommand extends Command {

	protected Command _baseCommand;
	private EntityResolver _resolver;

	public ResolvableCommand(Command baseCommand) {
		_baseCommand = baseCommand;
		_resolver = new AnnotatedEntityResolver();
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public Fields execute(Fields input) throws CommandException {
		Loggers.xsystem.info("EXECUTING Command:" + this.getClass().getSimpleName());	
		Loggers.xsystem.info("INPUT: " + input.asString());
		
		boolean doResolve = false;
		if (input.contains(GenericFieldNames.RESOLVE_ENTITIES)) {
			doResolve = Boolean.parseBoolean(input.stringNamed(GenericFieldNames.RESOLVE_ENTITIES));
		}
		
		Fields unresolvedResult = _baseCommand.execute(input);
		
		Fields result;
		if (doResolve) {
			ArrayList<DomainEntity> entities = unresolvedResult.valueFor(GenericFieldNames.RESPONSE, ArrayList.class);
			ArrayList<DomainEntity> resolvedEntities = new ArrayList<DomainEntity>();
			
			try {
				for (DomainEntity anEntity : entities) {
					DomainEntity aResolvedEntity = _resolver.solve(anEntity, anEntity.getClass());
					resolvedEntities.add(aResolvedEntity);
				}
				
				result = Fields.single(GenericFieldNames.RESPONSE, resolvedEntities);
			} catch (Exception exception) {
				throw CommandExceptionsFactory.createContainerException(exception);
			}
		} else {
			result = unresolvedResult;
		}
		
		Loggers.xsystem.info("OUTPUT after execution: " + result.asString());
		Loggers.xsystem.info("END Command:" + this.getClass().getSimpleName());	
		return result;
	}

	public <DE extends DomainEntity> ResolvableCommand with(Repository<DE> repository, Class<DE> domainEntityClass) {
		_resolver.with(repository, domainEntityClass);
		return this;
	}

	public ResolvableCommand maxDepth(int depthLimit) {
		_resolver.maxDepth(depthLimit);
		return this;
	}
	
	@Override
	public String getName() {
		return _baseCommand.getName() + '-' + GenericCommandNames.RESOLVABLE;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Fields componingInputKeys() {
		Fields componingInputKeys = _baseCommand.componingInputKeys();
		
		ParameterEntity parameter = new ParameterEntity(Fields.empty());
		parameter.set(ParameterEntity.NAME, GenericFieldNames.RESOLVE_ENTITIES);
		parameter.set(ParameterEntity.HTTP_PARAMETER_TYPE, GenericFieldValues.QUERY_STRING);
		
		ArrayList<ParameterEntity> inputs = (ArrayList<ParameterEntity>) componingInputKeys.valueFor(GenericFieldNames.INPUT);
		inputs.add(parameter);
		
		componingInputKeys.put(GenericFieldNames.INPUT, inputs);
		
		return componingInputKeys;
	}

	@Override
	public Fields componingOutputKeys() {
		return _baseCommand.componingOutputKeys();
	}
}
