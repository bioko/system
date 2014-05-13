package org.biokoframework.system.services.cron.mock;

import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.utils.fields.Fields;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-05-07
 */
public class SuccessfulCommand extends AbstractCommand {

    public static boolean EXECUTED;

    @Override
    public Fields execute(Fields input) throws CommandException {
        logInput(input);

        EXECUTED = true;

        logOutput();
        return new Fields();
    }


}
