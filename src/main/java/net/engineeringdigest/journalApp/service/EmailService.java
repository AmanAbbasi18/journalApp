package net.engineeringdigest.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            if (javaMailSender != null) {
                SimpleMailMessage mail = new SimpleMailMessage();
                mail.setTo(to);
                mail.setSubject(subject);
                mail.setText(body);
                javaMailSender.send(mail);
            } else {
                log.warn("Mail sender not configured. Skipping email.");
            }
        } catch (Exception e) {
            log.error("Exception while sending mail : ", e);
        }
    }
}
