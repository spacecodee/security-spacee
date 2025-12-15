package com.spacecodee.securityspacee.user.infrastructure.email;

import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.spacecodee.securityspacee.user.application.port.out.IEmailServicePort;

public final class SmtpEmailAdapter implements IEmailServicePort {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailAdapter.class);
    private static final String DEFAULT_FROM = "noreply@securityspacee.com";
    private static final String EMAIL_TEMPLATE = "%s%n%n%s%n%n%s";

    private final JavaMailSender mailSender;
    private final MessageSource messageSource;

    public SmtpEmailAdapter(JavaMailSender mailSender, MessageSource messageSource) {
        this.mailSender = mailSender;
        this.messageSource = messageSource;
    }

    @Override
    public void sendWelcomeEmail(@NonNull String toEmail, @NonNull String username) {
        Locale locale = Locale.getDefault();

        String subject = messageSource.getMessage("user.email.welcome.subject", null, locale);
        String greeting = messageSource.getMessage("user.email.welcome.greeting", new Object[]{username}, locale);
        String body = messageSource.getMessage("user.email.welcome.body", null, locale);
        String footer = messageSource.getMessage("user.email.welcome.footer", null, locale);

        String content = String.format(EMAIL_TEMPLATE, greeting, body, footer);

        sendEmail(toEmail, subject, content);
        log.info("Welcome email sent to: {}", toEmail);
    }

    @Override
    public void sendPasswordResetEmail(@NonNull String toEmail, @NonNull String resetToken) {
        Locale locale = Locale.getDefault();

        String subject = messageSource.getMessage("user.email.password_reset.subject", null, locale);
        String greeting = messageSource.getMessage("user.email.password_reset.greeting",
                new Object[]{toEmail}, locale);
        String body = messageSource.getMessage("user.email.password_reset.body",
                new Object[]{resetToken}, locale);
        String footer = messageSource.getMessage("user.email.password_reset.footer", null, locale);

        String content = String.format(EMAIL_TEMPLATE, greeting, body, footer);

        sendEmail(toEmail, subject, content);
        log.info("Password reset email sent to: {}", toEmail);
    }

    @Override
    public void sendEmailVerificationEmail(@NonNull String toEmail, @NonNull String verificationToken) {
        Locale locale = Locale.getDefault();

        String subject = messageSource.getMessage("user.email.verification.subject", null, locale);
        String greeting = messageSource.getMessage("user.email.verification.greeting",
                new Object[]{toEmail}, locale);
        String body = messageSource.getMessage("user.email.verification.body",
                new Object[]{verificationToken}, locale);
        String footer = messageSource.getMessage("user.email.verification.footer", null, locale);

        String content = String.format(EMAIL_TEMPLATE, greeting, body, footer);

        sendEmail(toEmail, subject, content);
        log.info("Email verification sent to: {}", toEmail);
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(DEFAULT_FROM);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}
