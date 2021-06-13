package ua.nure.moleculis.components;

import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import ua.nure.moleculis.models.entitys.User;
import ua.nure.moleculis.services.UserService;

@Component
public class UserRegistrationListener implements
        ApplicationListener<OnRegistrationCompleteEvent> {

    private final JavaMailSender mailSender;

    public UserRegistrationListener(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = event.getToken();

        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl
                = "https://moleculis-24e8a.web.app/registration-confirm?token=" + token;
        String message = Translator.toLocale("emailConfMes");

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + ": " + confirmationUrl);
        mailSender.send(email);
    }
}