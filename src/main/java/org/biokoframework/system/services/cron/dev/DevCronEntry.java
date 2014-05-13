package org.biokoframework.system.services.cron.dev;

import com.google.common.base.Objects;
import org.biokoframework.system.services.cron.ICronListener;

import java.util.List;

/**
* @author Mikol Faro <mikol.faro@gmail.com>
* @date 2014-05-13
*/
public final class DevCronEntry {
    private final Class<?> fCommand;
    private final String fCronExpression;
    private final String fNotificationEmail;
    private final List<ICronListener> fListeners;

    public DevCronEntry(Class<?> command, String cronExpression, String notificationEmail, List<ICronListener> listeners) {
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

    /**
     * Discards listeners lists from comparison
     */
    @Override
    public boolean equals(Object object) {
        return object instanceof DevCronEntry &&
                Objects.equal(fCommand, ((DevCronEntry) object).fCommand) &&
                Objects.equal(fCronExpression, ((DevCronEntry) object).fCronExpression) &&
                Objects.equal(fNotificationEmail, ((DevCronEntry) object).fNotificationEmail);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("command", fCommand.getName())
                .add("cronExpression", fCronExpression)
                .add("notificationEmail", fNotificationEmail)
                .toString();
    }
}
