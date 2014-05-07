package org.biokoframework.system.services.cron.mock;

import org.biokoframework.system.command.ICommand;
import org.biokoframework.system.services.cron.ICronListener;

/**
* @author Mikol Faro <mikol.faro@gmail.com>
* @date 2014-05-07
*/
public final class MockListener implements ICronListener {

    private Class<? extends ICommand> fCommand = null;
    private Throwable fCause = null;

    @Override
    public <C extends ICommand> void commandFinished(Class<C> command) {
        fCommand = command;
    }

    @Override
    public <C extends ICommand> void commandFailed(Class<C> command, Throwable cause) {
        fCommand = command;
        fCause = cause;
    }

    public Class<? extends ICommand> getCommand() {
        return fCommand;
    }

    public Throwable getCause() {
        return fCause;
    }
}
