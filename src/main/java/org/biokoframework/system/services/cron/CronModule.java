package org.biokoframework.system.services.cron;

import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.services.cron.impl.QuartzCronService;
import org.biokoframework.system.services.injection.ServiceModule;

import com.google.inject.Singleton;

public class CronModule extends ServiceModule {

	public CronModule(ConfigurationEnum config) {
		super(config);
	}

	@Override
	protected void configureForDev() {
		configureForDemo();
	}

	@Override
	protected void configureForDemo() {
		configureForProd();
	}

	@Override
	protected void configureForProd() {
		bind(ICronService.class).to(QuartzCronService.class).in(Singleton.class);
	}

}
