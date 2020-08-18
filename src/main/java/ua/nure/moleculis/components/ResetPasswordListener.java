package ua.nure.moleculis.components;

import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import ua.nure.moleculis.models.entitys.User;

@Component
public class ResetPasswordListener implements
        ApplicationListener<OnResetPasswordEvent> {

    private final JavaMailSender mailSender;

    public ResetPasswordListener(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(OnResetPasswordEvent event) {
        this.confirmResetPassword(event);
    }

    private void confirmResetPassword(OnResetPasswordEvent event) {
        final User user = event.getUser();
        final String token = event.getToken();

        final String recipientAddress = user.getEmail();
        final String subject = Translator.toLocale("passwordReset");
        final String confirmationUrl
                = "localhost:3000/reset-pass?token=" + token;
        final String message = Translator.toLocale("resetPassMes");

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + ": " + confirmationUrl);
        mailSender.send(email);
    }
}