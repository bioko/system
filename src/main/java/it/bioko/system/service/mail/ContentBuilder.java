package it.bioko.system.service.mail;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import it.bioko.system.command.CommandException;
import it.bioko.system.entity.template.Template;
import it.bioko.system.exceptions.CommandExceptionsFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

public class ContentBuilder {

	private Template _template;
	private Map<String, Object> _contentMap;

	public ContentBuilder(Template template, Map<String, Object> contentMap) {
		_template = template;
		_contentMap = contentMap;
	}

	public String buildBody() throws CommandException {
		return build(_template.get(Template.BODY));
	}
	
	public String buildTitle() throws CommandException {
		return build(_template.get(Template.TITLE));
	}

	private String build(String template) throws CommandException {
		// Freemarker
		StringReader stringReader = new StringReader(template);
		StringWriter stringWriter = new StringWriter();
		
		Configuration config = new Configuration();

		freemarker.template.Template freemarkerTemplate;
		try {
			freemarkerTemplate = new freemarker.template.Template("", stringReader, config);
			freemarkerTemplate.process(_contentMap, stringWriter);
		} catch (IOException exception) {
			throw CommandExceptionsFactory.createContainerException(exception);
		} catch (TemplateException exception) {
			throw CommandExceptionsFactory.createContainerException(exception);
		}
		
		return stringWriter.toString();
	}

}
