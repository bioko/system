package org.biokoframework.system.services.cron.dev;

import com.google.inject.Injector;
import com.google.inject.name.Named;
import org.biokoframework.system.KILL_ME.exception.SystemException;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.command.ICommand;
import org.biokoframework.system.services.cron.CronException;
import org.biokoframework.system.services.cron.ICronListener;
import org.biokoframework.system.services.cron.ICronLocator;
import org.biokoframework.system.services.cron.ICronService;
import org.biokoframework.system.services.cron.impl.CronFailureNotifier;
import org.biokoframework.system.services.email.IEmailService;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-05-13
 */
@Singleton
public class DevCronService implements ICronService {

    public static final String ADMIN_EMAIL = "notify-me@example.com";
    public static DevCronService INSTANCE;

    private final IEmailService fMailService;
    private final String fCronEmailAddress;
    private final Injector fInjector;

    private Map<Class<? extends ICommand>, DevCronEntry> fEntries = new HashMap<>();

    @Inject
    public DevCronService(IEmailService mailService, @Named("cronEmailAddress") String cronEmailAddress, Injector injector, @Nullable ICronLocator cronLocator) {
        fMailService = mailService;
        fCronEmailAddress = cronEmailAddress;
        fInjector = injector;

        if (cronLocator != null) {
            try {
                cronLocator.locateAndRegisterAll(this);
            } catch (CronException exception) {
                throw new RuntimeException(exception);
            }
        }

        INSTANCE = this;
    }

    @Override
    public <C extends ICommand> void schedule(Class<C> command, String cronExpression, String notificationEmail) throws CronException {
        schedule(command, cronExpression, notificationEmail, new ArrayList<ICronListener>());
    }

    @Override
    public <C extends ICommand> void schedule(Class<C> command, String cronExpression, String notificationEmail, List<ICronListener> listeners) throws CronException {
        listeners = new ArrayList<>(listeners);
        listeners.add(new CronFailureNotifier(fMailService, fCronEmailAddress, notificationEmail));

        fEntries.put(command, new DevCronEntry(command, cronExpression, notificationEmail, listeners));
    }

    @Override
    public void stop() throws SystemException {
    }

    public <C extends ICommand> void trigger(Class<C> command) {
        execute(fEntries.get(command));
    }

    private void execute(DevCronEntry devCronEntry) {
        Class<? extends ICommand> commandClass = (Class<? extends ICommand>) devCronEntry.getCommand();
        ICommand command = fInjector.getInstance(commandClass);

        try {
            command.execute(new Fields());

            notifySuccess(commandClass, devCronEntry.getListeners());
        } catch (CommandException | ValidationException exception) {
            notifyFailure(commandClass, exception, devCronEntry.getListeners());
        }
    }

    private void notifySuccess(Class<? extends ICommand> commandClass, List<ICronListener> listeners) {
        for (ICronListener aListener : listeners) {
            aListener.commandFinished(commandClass);
        }
    }

    private void notifyFailure(Class<? extends ICommand> commandClass, Throwable cause, List<ICronListener> listeners) {
        for (ICronListener aListener : listeners) {
            aListener.commandFailed(commandClass, cause);
        }
    }
}
