package it.bioko.system.repository.core;

import it.bioko.system.command.ValidationException;
import it.bioko.system.repository.core.query.Query;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.fields.Fields;

import java.util.ArrayList;
import java.util.List;

public abstract class Repository<T extends DomainEntity> {

	public abstract T save(T anEntity) throws ValidationException, RepositoryException;

	public abstract T delete(String anEntityKey);

	public abstract T retrieve(String anEntityKey);

	public abstract T retrieveByForeignKey(String foreignKeyName, String foreignKeyValue);
	
//	public abstract T retrieveByForeignKey(String foreignKeyName, String foreignKeyValue, boolean ignoreCase);

	public abstract ArrayList<T> getEntitiesByForeignKey(String foreignKeyName, String foreignKeyValue);
	
//	public abstract ArrayList<T> getEntitiesByForeignKey(String foreignKeyName, String foreignKeyValue, boolean ignoreCase);

	@Deprecated
	public final boolean contains(T anEntity) {
		return false;
	}

	public abstract String report();

	public abstract ArrayList<T> getAll();

	public List<T> call(DomainEntity aDomainEntity, String aMethod) throws ValidationException, RepositoryException {
		T repoEntity = (T) aDomainEntity;
		
		ArrayList<T> result = new ArrayList<T>();
		// TODO colpetto di reflection per estrarre il nome del metodo 
		// da invocare, associato ai metodi rest
		if (aMethod.equals("SAVE")) {
			T entity = save(repoEntity);
			if (entity != null) { 
				result.add(entity);
			}
		} else if (aMethod.equals("RETRIEVE")) {
			if (repoEntity.getId() != null) {
				T entity = retrieve(repoEntity.getId());
				if (entity != null)
					result.add(entity);
			} else {
				System.out.println(repoEntity.fields().report());
				String filledFieldKey = getFirstNotVoidFieldKey(repoEntity.fields());
				if (filledFieldKey==null) {
					result = getAll();
				} else {
					result = getEntitiesByForeignKey(filledFieldKey, repoEntity.get(filledFieldKey));
				}
				
//				result = getAll();
			}
		} else if (aMethod.equals("DELETE")) {
			T deleted = delete(repoEntity.getId());
			if (deleted != null)
				result.add(deleted);
		}
		return result;
	}

	public abstract T retrieve(T anEntityT);
	
	// TODO togliere quando Mattia farà il query builder,
	// per ora restringo solo sul primo campo pieno
	private String getFirstNotVoidFieldKey(Fields fields) {
		String foundKey=null;
		for (String fieldKey: fields.keys()) {
			if (fields.contains(fieldKey)) {
				foundKey=fieldKey;
				break;
			}
		}
		
		return foundKey;
	}
	public abstract Query<T> createQuery();

}