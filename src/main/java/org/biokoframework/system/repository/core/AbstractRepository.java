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

package org.biokoframework.system.repository.core;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.domain.annotation.field.ForeignKeysFactory;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.repository.RepositoryException;
import org.biokoframework.utils.repository.query.Query;
import org.biokoframework.utils.validation.ValidationErrorBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractRepository<DE extends DomainEntity> implements Repository<DE> {

    private static final Logger LOGGER = Logger.getLogger(AbstractRepository.class);

    protected final IEntityBuilderService fEntityBuilderService;
    protected final IRepositoryService fRepositoryService;
    private ArrayList<Map.Entry<String, Class<? extends DomainEntity>>> fForeignKeys = new ArrayList<>();

    public AbstractRepository(IEntityBuilderService entityBuilderService, IRepositoryService repositoryService) {
        fEntityBuilderService = entityBuilderService;
        fRepositoryService = repositoryService;
	}
	
	public List<DE> call(DomainEntity aDomainEntity, String aMethod) throws ValidationException, RepositoryException {
		DE repoEntity = (DE) aDomainEntity;
		
		ArrayList<DE> result = new ArrayList<DE>();
		// TODO colpetto di reflection per estrarre il nome del metodo 
		// da invocare, associato ai metodi rest
        switch (aMethod) {
            case "SAVE":
                DE entity = save(repoEntity);
                if (entity != null) {
                    result.add(entity);
                }
                break;
            case "RETRIEVE":
                if (repoEntity.getId() != null) {
                    DE retrieve = retrieve(repoEntity.getId());
                    if (retrieve != null)
                        result.add(retrieve);
                } else {
                    System.out.println(repoEntity.fields());
                    String filledFieldKey = getFirstNotVoidFieldKey(repoEntity.fields());
                    if (filledFieldKey == null) {
                        result = getAll();
                    } else {
                        result = getEntitiesByForeignKey(filledFieldKey, repoEntity.get(filledFieldKey).toString());
                    }

//				result = getAll();
                }
                break;
            case "DELETE":
                DE deleted = delete(repoEntity.getId());
                if (deleted != null)
                    result.add(deleted);
                break;
        }
		return result;
	}

    // TODO togliere quando Mattia farà il query builder,
	// per ora restringo solo sul primo campo pieno
	private String getFirstNotVoidFieldKey(Fields fields) {
		String foundKey=null;
		for (String fieldKey: fields.keys()) {
			if (fields.containsKey(fieldKey)) {
				foundKey=fieldKey;
				break;
			}
		}
		
		return foundKey;
	}

    public final DE save(DomainEntity anEntity) throws ValidationException, RepositoryException {
        ensureEntityValidation(anEntity);
        return saveAfterValidation(anEntity);
    }

    protected abstract DE saveAfterValidation(DomainEntity anEntity) throws ValidationException, RepositoryException;

    protected void ensureEntityValidation(DomainEntity aDomainEntity) throws ValidationException {
        if (!aDomainEntity.isValid()) {
            throw new ValidationException(aDomainEntity.getValidationErrors());
        }
        runForeignKeyChecks(aDomainEntity);
    }

    protected void runForeignKeyChecks(DomainEntity aDomainEntity) throws ValidationException {
        if (fForeignKeys.isEmpty()) {
            try {
                fForeignKeys = ForeignKeysFactory.create(aDomainEntity.getClass());
            } catch (IllegalAccessException exception) {
                LOGGER.fatal("[EASY MEN]: your entity is not very reflective", exception);
                throw new RuntimeException(exception);
            }
        }

        List<ErrorEntity> errors = new ArrayList<>();
        for (Map.Entry<String, Class<? extends DomainEntity>> aForeignKey : fForeignKeys) {

            String fieldName = aForeignKey.getKey();
            String foreignKeyValue = aDomainEntity.get(fieldName);
            if (!StringUtils.isEmpty(foreignKeyValue)) {
                DomainEntity foiregnEntity = fRepositoryService
                        .getRepository(aForeignKey.getValue())
                        .retrieve(foreignKeyValue);

                if (foiregnEntity == null) {
                    errors.add(ValidationErrorBuilder.buildForeignKeyError(fieldName));
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

	@Override
	public abstract Query<DE> createQuery();

	public DE getInstance(Class<DE> entityClass) {
		return fEntityBuilderService.getInstance(entityClass);
	}

}