package it.bioko.system.service.cron.annotation;

import it.bioko.system.ConfigurationEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CronExpression {
	String exp();
	ConfigurationEnum conf();
}
