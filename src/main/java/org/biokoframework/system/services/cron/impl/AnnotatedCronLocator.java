package org.biokoframework.system.services.cron.impl;

import org.biokoframework.system.services.cron.ICronLocator;
import org.biokoframework.system.services.cron.ICronService;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-05-07
 */
public class AnnotatedCronLocator implements ICronLocator {

    @Inject
    public AnnotatedCronLocator(@SuppressWarnings("rawtypes") @Named("Commands") Class commandsClass) {

    }

    @Override
    public void locateAndRegisterAll(ICronService cronService) {

    }

}
