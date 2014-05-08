package org.biokoframework.system.services.cron.impl;

import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.KILL_ME.commons.GenericFieldValues;
import org.biokoframework.system.services.cron.CronException;
import org.biokoframework.system.services.cron.annotation.Cron;
import org.biokoframework.system.services.cron.annotation.CronExpression;
import org.biokoframework.system.services.cron.mock.FailingCommand;
import org.biokoframework.system.services.cron.mock.MockCronService;
import org.biokoframework.system.services.cron.mock.MockCronService.MockCronEntry;
import org.biokoframework.system.services.cron.mock.SuccessfulCommand;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-05-08
 */
public class AnnotatedCronLocatorTest {

    @Test
    public void simpleAnnotatedTest() throws CronException {
        MockCronService mockCronService = new MockCronService();

        AnnotatedCronLocator locator = new AnnotatedCronLocator(DummyCronCommands.class, ConfigurationEnum.DEV);
        locator.locateAndRegisterAll(mockCronService);

        List<MockCronEntry> entries = mockCronService.getEntries();

        assertThat(entries, contains(
                new MockCronEntry(SuccessfulCommand.class, "10 * * * * ?", GenericFieldValues.CRON_EMAIL, null),
                new MockCronEntry(FailingCommand.class, "0/10 * * * * ?", GenericFieldValues.CRON_EMAIL, null)
        ));

        mockCronService = new MockCronService();
        locator = new AnnotatedCronLocator(DummyCronCommands.class, ConfigurationEnum.PROD);
        locator.locateAndRegisterAll(mockCronService);

        entries = mockCronService.getEntries();

        assertThat(entries, contains(
                new MockCronEntry(SuccessfulCommand.class, "0 0 10 * * ?", GenericFieldValues.CRON_EMAIL, null)
        ));

    }

    public class DummyCronCommands {

        @Cron(impl = SuccessfulCommand.class, notifyTo= GenericFieldValues.CRON_EMAIL,
                expressions={
                        @CronExpression(exp="10 * * * * ?", conf= ConfigurationEnum.DEV),
                        @CronExpression(exp="0 0 10 * * ?", conf=ConfigurationEnum.PROD),
                }
        )
        public static final String CRON_EXAMPLE_COMMAND = "cron-example-command";

        @Cron(impl = FailingCommand.class, notifyTo=GenericFieldValues.CRON_EMAIL,
                expressions={
                        @CronExpression(exp="0/10 * * * * ?", conf=ConfigurationEnum.DEV),
                }
        )
        public static final String CRON_FAILING_COMMAND = "cron-failing-command";

    }

}
