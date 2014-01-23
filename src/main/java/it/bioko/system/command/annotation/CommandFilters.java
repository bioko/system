package it.bioko.system.command.annotation;

import it.bioko.system.command.AbstractFilter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandFilters {
	Class<? extends AbstractFilter>[] value();
}
