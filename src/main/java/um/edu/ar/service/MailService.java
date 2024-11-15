package um.edu.ar.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import tech.jhipster.config.JHipsterProperties;
import um.edu.ar.domain.User;

/**
 * Service for sending emails asynchronously.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
public class MailService {

    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

    private static final String USER = "user";
    private static final String BASE_URL = "baseUrl";

    private final JHipsterProperties jHipsterProperties;
    private final JavaMailSender javaMailSender;
    private final MessageSource messageSource;
    private final SpringTemplateEngine templateEngine;

    public MailService(
        JHipsterProperties jHipsterProperties,
        JavaMailSender javaMailSender,
        MessageSource messageSource,
        SpringTemplateEngine templateEngine
    ) {
        LOG.info("Initializing MailService");
        this.jHipsterProperties = jHipsterProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        LOG.debug("Request to send asynchronous email to: {}", to);
        this.sendEmailSync(to, subject, content, isMultipart, isHtml);
    }

    private void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        LOG.debug("Preparing to send email [multipart: {}, html: {}] to: {} with subject: {}", isMultipart, isHtml, to, subject);
        LOG.debug("Email content: {}", content);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            LOG.debug("Creating MimeMessageHelper");
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(jHipsterProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, isHtml);

            LOG.debug("Sending email message");
            javaMailSender.send(mimeMessage);
            LOG.info("Email successfully sent to: {}", to);
        } catch (MailException | MessagingException e) {
            LOG.error("Failed to send email to: {}. Error: {}", to, e.getMessage());
            LOG.debug("Detailed error information", e);
        }
    }

    @Async
    public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        LOG.debug("Request to send template email to user: {}", user.getLogin());
        this.sendEmailFromTemplateSync(user, templateName, titleKey);
    }

    private void sendEmailFromTemplateSync(User user, String templateName, String titleKey) {
        if (user.getEmail() == null) {
            LOG.warn("Cannot send email to user '{}' - no email address found", user.getLogin());
            return;
        }

        LOG.debug("Preparing template email for user: {}", user.getLogin());
        Locale locale = Locale.forLanguageTag(user.getLangKey());

        LOG.debug("Setting up template context with locale: {}", locale);
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());

        LOG.debug("Processing email template: {}", templateName);
        String content = templateEngine.process(templateName, context);

        LOG.debug("Getting email subject from message source with key: {}", titleKey);
        String subject = messageSource.getMessage(titleKey, null, locale);

        LOG.debug("Sending template email to user");
        this.sendEmailSync(user.getEmail(), subject, content, false, true);
    }

    @Async
    public void sendActivationEmail(User user) {
        LOG.debug("Request to send activation email to user: {} ({})", user.getLogin(), user.getEmail());
        this.sendEmailFromTemplateSync(user, "mail/activationEmail", "email.activation.title");
        LOG.info("Activation email queued for user: {}", user.getLogin());
    }

    @Async
    public void sendCreationEmail(User user) {
        LOG.debug("Request to send creation email to user: {} ({})", user.getLogin(), user.getEmail());
        this.sendEmailFromTemplateSync(user, "mail/creationEmail", "email.activation.title");
        LOG.info("Creation email queued for user: {}", user.getLogin());
    }

    @Async
    public void sendPasswordResetMail(User user) {
        LOG.debug("Request to send password reset email to user: {} ({})", user.getLogin(), user.getEmail());
        this.sendEmailFromTemplateSync(user, "mail/passwordResetEmail", "email.reset.title");
        LOG.info("Password reset email queued for user: {}", user.getLogin());
    }
}
