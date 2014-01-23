package it.bioko.system.repository.sql.translator.annotation;

import it.bioko.system.repository.sql.translator.annotation.impl.PlainTranslator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Translate {

	Class<?> from();
	Class<? extends Translator> using() default PlainTranslator.class;
	String to() default "";
	
}
