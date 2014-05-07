package org.biokoframework.system.services.cron.mock;

import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.utils.fields.Fields;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-05-07
 */
public class FailingCommand extends AbstractCommand {

    @Override
    public Fields execute(Fields input) throws CommandException {
        logInput(input);

        throw CommandExceptionsFactory.createBadCommandInvocationException();
    }

}
