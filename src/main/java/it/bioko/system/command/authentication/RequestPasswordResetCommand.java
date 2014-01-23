package it.bioko.system.command.authentication;

import it.bioko.system.KILL_ME.commons.GenericConstants;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericRepositoryNames;
import it.bioko.system.command.Command;
import it.bioko.system.command.CommandException;
import it.bioko.system.entity.authentication.PasswordReset;
import it.bioko.system.entity.login.Login;
import it.bioko.system.entity.template.Template;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.system.repository.core.Repository;
import it.bioko.system.repository.core.SafeRepositoryHelper;
import it.bioko.system.service.currenttime.CurrentTimeService;
import it.bioko.system.service.mail.ContentBuilder;
import it.bioko.system.service.mail.EmailFiller;
import it.bioko.system.service.mail.EmailServiceImplementation;
import it.bioko.system.service.random.RandomGeneratorService;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.fields.Fields;
import it.bioko.utils.validator.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

public class RequestPasswordResetCommand extends Command {

	public static final String PASSWORD_RESET_TOKEN = "passwordResetToken";

	public static final String PASSWORD_RESET_MAIL_TEMPLATE = "passwordResetMailTemplate";

	private static final String RESET_PASSWORD_LANDING_PAGE_URL = "resetPasswordLandingPage";
	
	private Repository<Login> _loginRepo;
	private Repository<PasswordReset> _passwordResetRepo;
	private Repository<Template> _templateRepo;
	private CurrentTimeService _currentTimeService;
	private RandomGeneratorService _randomTokenService;


	@Override
	public void onContextInitialized() {
		_loginRepo = _context.getRepository(GenericRepositoryNames.LOGIN_REPOSITORY);
		_passwordResetRepo = _context.getRepository(GenericRepositoryNames.PASSWORD_RESET);
		
		_templateRepo = _context.getRepository(GenericRepositoryNames.TEMPLATE);
		
		_currentTimeService = (CurrentTimeService) _context.get(GenericConstants.CONTEXT_CURRENT_TIME_SERVICE);
		_randomTokenService = (RandomGeneratorService) _context.get(GenericConstants.CONTEXT_RANDOM_GENERATOR_SERVICE);
	}
	
	@Override
	public Fields execute(Fields input) throws CommandException {
		logInput(input);
		
		String userEmail = input.stringNamed(Login.USER_EMAIL);
		Login login = _loginRepo.retrieveByForeignKey(Login.USER_EMAIL, userEmail);
		if (login == null) {
			throw CommandExceptionsFactory.createEntityNotFound(Login.class.getSimpleName(), Login.USER_EMAIL, userEmail);
		}
		
		DateTime now = _currentTimeService.getCurrentTimeAsDateTime();
		
		PasswordReset passwordReset = new PasswordReset(Fields.empty());
		passwordReset.set(PasswordReset.LOGIN_ID, login.getId());
		passwordReset.set(PasswordReset.TOKEN_EXPIRATION, now.plusDays(1).toString(Validator.ISO_TIMESTAMP));
		String randomToken = _randomTokenService.generateString(PASSWORD_RESET_TOKEN, 20);
		passwordReset.set(PasswordReset.TOKEN, randomToken);
		SafeRepositoryHelper.save(_passwordResetRepo, passwordReset, _context);


		Template mailTemplate = _templateRepo.retrieveByForeignKey(Template.TRACK, PASSWORD_RESET_MAIL_TEMPLATE);
		if (mailTemplate != null) {
			
			Map<String, Object> contentMap = new HashMap<String, Object>();
			contentMap.put("url", StringUtils.defaultString(_context.getSystemProperty(RESET_PASSWORD_LANDING_PAGE_URL)));
			contentMap.put("token", randomToken);
			contentMap.put("userEmail", login.get(Login.USER_EMAIL));
			ContentBuilder contentBuilder = new ContentBuilder(mailTemplate, contentMap);
			
			EmailFiller filler = new EmailFiller();
			filler.addTo(login.get(Login.USER_EMAIL));
			filler.setFrom("no-reply@engaged.it");
			filler.setContent(contentBuilder.buildBody());
			filler.setSubject(contentBuilder.buildTitle());
			
			EmailServiceImplementation dispatcher = EmailServiceImplementation.mailServer();
			MimeMessage message = dispatcher.newMessage();
			
			filler.fill(message);
			
			dispatcher.send(message);
		} else {
			throw CommandExceptionsFactory.createEntityNotFound(Template.class.getSimpleName(), Template.TRACK, PASSWORD_RESET_MAIL_TEMPLATE);
		}

		logOutput();
		return Fields.single(GenericFieldNames.RESPONSE, new ArrayList<DomainEntity>());
	}

}
