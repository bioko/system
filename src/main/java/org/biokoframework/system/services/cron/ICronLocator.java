package org.biokoframework.system.services.cron;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-05-07
 */
public interface ICronLocator {

    public void locateAndRegisterAll(ICronService cronService) throws CronException;

}
