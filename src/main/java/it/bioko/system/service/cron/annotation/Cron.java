package it.bioko.system.service.cron.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cron {
	String notifyTo() default "";
	String input() default "";
	CronExpression[] expressions();
}
