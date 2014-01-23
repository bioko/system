package it.bioko.system.command.annotation;

import it.bioko.system.ConfigurationEnum;
import it.bioko.system.KILL_ME.commons.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	Class<? extends it.bioko.system.command.Command> impl();
	HttpMethod rest(); 
	
	ConfigurationEnum[] hideOn() default {};
}
