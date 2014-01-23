package it.bioko.system.entity.template;

import it.bioko.utils.domain.EntityBuilder;


public class TemplateBuilder extends EntityBuilder<Template> {

	public static String DEFAULT_ID = "555";
	public static String DEFAULT_TEMPLATE_TITLE = "";
	public static String DEFAULT_TEMPLATE_HTML = "";
	public static String _templateTitle = DEFAULT_TEMPLATE_TITLE;
	public static String _templateEmailTrackId = "1";
	public static String _templateHtml = DEFAULT_TEMPLATE_HTML;

	public static final String EXAMPLE = "example";
	private static final String EXAMPLE_JSON = "{'id':'555','title':'','track':'1','body':''}";
	
	public static final String SYSTEM_A_TEMPLATE = "systemAtemplate";

	public TemplateBuilder() {
		super(Template.class);
		putExample(EXAMPLE, EXAMPLE_JSON);
		putExample(SYSTEM_A_TEMPLATE, "{'track':'passwordResetMailTemplate','title':'Password reset','body':'Abbiamo ricevuto una richiesta di reset della password. Clicca <a href=\\\"http://local.engaged.it/password-reset&token=${token}\\\">qui</a> per resettare la tua password'}");
	}

	@Override
	public EntityBuilder<Template> loadDefaultExample() {
	return loadExample(EXAMPLE);
	}
}
