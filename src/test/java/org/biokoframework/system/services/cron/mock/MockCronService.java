package org.biokoframework.system.services.cron.mock;

import org.biokoframework.system.KILL_ME.exception.SystemException;
import org.biokoframework.system.command.ICommand;
import org.biokoframework.system.services.cron.CronException;
import org.biokoframework.system.services.cron.ICronListener;
import org.biokoframework.system.services.cron.ICronService;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-05-08
 */
public class MockCronService implements ICronService {

    private List<MockCronEntry> fEntries = new LinkedList<>();

    @Override
    public <C extends ICommand> void schedule(Class<C> command, String cronExpression, String notificationEmail) throws CronException {
        fEntries.add(new MockCronEntry(command, cronExpression, notificationEmail, null));
    }

    @Override
    public <C extends ICommand> void schedule(Class<C> command, String cronExpression, String notificationEmail, List<ICronListener> listeners) throws CronException {
        fEntries.add(new MockCronEntry(command, cronExpression, notificationEmail, listeners));
    }

    @Override
    public void stop() throws SystemException {
    }

    public List<MockCronEntry> getEntries() {
        return fEntries;
    }

    public static final class MockCronEntry {
        private final Class<?> fCommand;
        private final String fCronExpression;
        private final String fNotificationEmail;
        private final List<ICronListener> fListeners;

        public MockCronEntry(Class<?> command, String cronExpression, String notificationEmail, List<ICronListener> listeners) {
            fCommand = command;
            fCronExpression = cronExpression;
            fNotificationEmail = notificationEmail;
            fListeners = listeners;
        }

        public Class<?> getCommand() {
            return fCommand;
        }

        public String getCronExpression() {
            return fCronExpression;
        }

        public String getNotificationEmail() {
            return fNotificationEmail;
        }

        public List<ICronListener> getListeners() {
            return fListeners;
        }

        @Override
        public boolean equals(Object object) {
            return object instanceof MockCronEntry &&
                    Objects.equals(fCommand, ((MockCronEntry) object).fCommand) &&
                    Objects.equals(fCronExpression, ((MockCronEntry) object).fCronExpression) &&
                    Objects.equals(fNotificationEmail, ((MockCronEntry) object).fNotificationEmail) &&
                    Objects.equals(fListeners, ((MockCronEntry) object).fListeners);
        }
    }
}
