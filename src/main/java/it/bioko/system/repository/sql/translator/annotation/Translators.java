package it.bioko.system.repository.sql.translator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Translators {
	Translate[] collection();
	Class<? extends Translator> idTranslator();
}
