package it.bioko.system.command.crud.annotation;

import it.bioko.system.ConfigurationEnum;
import it.bioko.utils.domain.DomainEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CrudCommand {
	Class<? extends DomainEntity> entity();
	String repoName();
	boolean create() default true;
	boolean read() default true;
	boolean update() default true;
	boolean delete() default true;
	boolean head() default false;
	boolean describe() default true;
	ConfigurationEnum[] hideOn() default {};
}
