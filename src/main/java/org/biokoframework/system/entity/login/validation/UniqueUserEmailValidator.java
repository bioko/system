package org.biokoframework.system.entity.login.validation;

import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.validation.impl.AbstractAdditionalValidator;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.repository.Repository;

import javax.inject.Inject;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-03-27
 */
public class UniqueUserEmailValidator extends AbstractAdditionalValidator {

    private final Repository<Login> fLoginRepo;

    @Inject
    public UniqueUserEmailValidator(IRepositoryService repos) {
        fLoginRepo = repos.getRepository(Login.class);
    }

    @Override
    public boolean isValid(DomainEntity entity) {
        String userEmail = entity.get(Login.USER_EMAIL);

        Login existingLogin = fLoginRepo.retrieveByForeignKey(Login.USER_EMAIL, userEmail);
        if (existingLogin == null) {
            clearError();
            return true;
        } else if (existingLogin.getId() == entity.getId()) {
            clearError();
            return true;
        } else {
            setError(FieldNames.ENTITY_FIELD_NOT_UNIQUE, "Field should be unique", Login.USER_EMAIL);
            return false;
        }
    }

}
