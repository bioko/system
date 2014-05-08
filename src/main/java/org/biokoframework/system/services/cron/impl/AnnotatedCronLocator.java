package org.biokoframework.system.services.cron.impl;

import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.services.cron.CronException;
import org.biokoframework.system.services.cron.ICronLocator;
import org.biokoframework.system.services.cron.ICronService;
import org.biokoframework.system.services.cron.annotation.Cron;
import org.biokoframework.system.services.cron.annotation.CronExpression;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-05-07
 */
public class AnnotatedCronLocator implements ICronLocator {

    private final ConfigurationEnum fConfig;
    private final List<Cron> fCronEntries;

    @Inject
    public AnnotatedCronLocator(@SuppressWarnings("rawtypes") @Named("Commands") Class commandsClass, ConfigurationEnum config) {
        fConfig = config;

        fCronEntries = findCronEntries(commandsClass);
    }

    private List<Cron> findCronEntries(Class commandsClass) {
        List<Cron> cronEntries = new LinkedList<>();
        for (Field aField : commandsClass.getFields()) {
            Cron aCronEntry = aField.getAnnotation(Cron.class);
            if (aCronEntry != null) {
                cronEntries.add(aCronEntry);
            }
        }
        return cronEntries;
    }

    @Override
    public void locateAndRegisterAll(ICronService cronService) throws CronException {
        for (Cron aCronEntry : fCronEntries) {
            String expression = findExpression(aCronEntry, fConfig);
            if (expression != null) {
                cronService.schedule(aCronEntry.impl(), expression, aCronEntry.notifyTo());
            }
        }
    }

    private String findExpression(Cron aCronEntry, ConfigurationEnum config) {
        for (CronExpression anExpression : aCronEntry.expressions()) {
            if (config.equals(anExpression.conf())) {
                return anExpression.exp();
            }
        }
        return null;
    }


}
