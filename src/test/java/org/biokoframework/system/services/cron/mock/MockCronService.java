package org.biokoframework.system.services.cron.mock;

import org.biokoframework.system.KILL_ME.exception.SystemException;
import org.biokoframework.system.command.ICommand;
import org.biokoframework.system.services.cron.CronException;
import org.biokoframework.system.services.cron.ICronListener;
import org.biokoframework.system.services.cron.ICronService;
import org.biokoframework.system.services.cron.dev.DevCronEntry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-05-08
 */
public class MockCronService implements ICronService {

    private List<DevCronEntry> fEntries = new LinkedList<>();

    @Override
    public <C extends ICommand> void schedule(Class<C> command, String cronExpression, String notificationEmail) throws CronException {
        schedule(command, cronExpression, notificationEmail, new ArrayList<ICronListener>());
    }

    @Override
    public <C extends ICommand> void schedule(Class<C> command, String cronExpression, String notificationEmail, List<ICronListener> listeners) throws CronException {
        fEntries.add(new DevCronEntry(command, cronExpression, notificationEmail, listeners));
    }

    @Override
    public void stop() throws SystemException {
    }

    public List<DevCronEntry> getEntries() {
        return fEntries;
    }

}
