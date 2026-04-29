package net.engineeringdigest.journalApp.Service;

import net.engineeringdigest.journalApp.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTests {

    @Autowired
    private EmailService emailService;

    @Value("${app.personal.email}")
    private String testEmail;

    @Test
    public void testSendMail() {
        emailService.sendEmail(testEmail, "Testing Java Mail Sender", "Hi aap kaise he");
    }
}
