package org.biokoframework.system.services.queue.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.repository.memory.InMemoryRepository;
import org.biokoframework.system.services.entity.EntityModule;
import org.biokoframework.system.services.queue.IQueueService;
import org.biokoframework.system.services.repository.RepositoryModule;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.RepositoryException;
import org.biokoframework.utils.validation.ValidationModule;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-05-16
 */
public class QueueServiceTest {

    private IQueueService fService;

    @Before
    public void createService() {
        Injector injector = Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(IQueueService.class).to(SqlQueueService.class);
                    }
                },
                new RepositoryModule(ConfigurationEnum.DEV) {
                    @Override
                    protected void configureForDev() {
                        bindRepositoryTo(InMemoryRepository.class);
                    }
                    @Override
                    protected void configureForDemo() {
                        configureForDev();
                    }
                    @Override
                    protected void configureForProd() {
                        configureForDev();
                    }
                },
                new EntityModule(),
                new ValidationModule());

        fService = injector.getInstance(IQueueService.class);
    }

    @Test
    public void simpleQueueTest() throws ValidationException, RepositoryException {

        // Push A in 1
        fService.push("1", "A");

        // Push B in 2
        fService.push("2", "B");

        // Pop A from 1
        String value = fService.pop("1");
        assertThat(value, is(equalTo("A")));

        // Pop B from 2
        value = fService.pop("2");
        assertThat(value, is(equalTo("B")));
    }

    @Test
    public void testFieldsAndStrings() throws ValidationException, RepositoryException {

        fService.push("1", "a");
        fService.pushFields("2", new Fields("a", "field"));

        Fields fields = fService.popFields("2");
        assertThat(fields.get("a"), is(Matchers.<Object>equalTo("field")));

        assertThat(fService.pop("1"), is(equalTo("a")));
    }

    @Test
    public void twoQueuesTest() throws ValidationException, RepositoryException {

        // Push A in 1
        fService.push("1", "A");

        // Push B in 2
        fService.push("2", "B");

        // Push C in 1
        fService.push("1", "C");

        // Pop B from 2
        String value = fService.pop("2");
        assertThat(value, is(equalTo("B")));

        // Pop A from 1
        value = fService.pop("1");
        assertThat(value, is(equalTo("A")));

        // Pop C from 1
        value = fService.pop("1");
        assertThat(value, is(equalTo("C")));

    }

}
