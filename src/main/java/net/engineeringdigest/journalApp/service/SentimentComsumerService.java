package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.model.SentimentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;

@Service
@Profile("dev")
public class SentimentComsumerService {

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "weekly-sentiments" , groupId = "weekly-sentiment-group")
    public void consume(SentimentData sentimentData) {
        System.out.println("Received: " + sentimentData);
        sendMail(sentimentData);
    }

    private void sendMail(SentimentData sentimentData) {
        emailService.sendEmail(sentimentData.getEmail() , "Sentiment for previous week" , sentimentData.getSentiment());
    }
}
