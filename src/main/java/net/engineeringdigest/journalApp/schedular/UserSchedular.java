package net.engineeringdigest.journalApp.schedular;

import net.engineeringdigest.journalApp.cache.AppCache;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.enums.Sentiment;
import net.engineeringdigest.journalApp.model.SentimentData;
import net.engineeringdigest.journalApp.repository.UserRepositoryImpl;
import net.engineeringdigest.journalApp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserSchedular {
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private AppCache appCache;

    @Autowired
    private KafkaTemplate<String, SentimentData> kafkaTemplate;    //the key and the data, is of SentimentData contains (email,sentiment)


    @Scheduled(cron = "0 0 9 ? * SUN")
    public void fetchUsersAndSendSaMail() {
        List<User> users = userRepository.getUserForSA();
        for(User user : users) {
            List<JournalEntry> journalEntries = user.getJournalEntries();
            List<Sentiment> sentiments = journalEntries.stream().filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))).map(x -> x.getSentiment()).collect(Collectors.toList());//toh hame filter karre journal entries ka content wo jo aaj ke din se 7 din paile ke badh wala data he wo sab deo list ke form me
            Map<Sentiment , Integer> sentimentCounts = new HashMap<>();
            for(Sentiment sentiment : sentiments) {
                if(sentiment != null)
                    sentimentCounts.put(sentiment , sentimentCounts.getOrDefault(sentiment , 0) + 1);
            }

            //now based on the count get the most frequent sentiment of the user
            Sentiment mostFreqSentiment = null;
            int maxCount = 0;
            for(Map.Entry<Sentiment , Integer> entry : sentimentCounts.entrySet()) {
                if(entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostFreqSentiment = entry.getKey();
                }
            }
            if(mostFreqSentiment != null) {
                SentimentData sentimentData = SentimentData.builder().email(user.getEmail()).sentiment("Sentiment for last 7 days : " + mostFreqSentiment).build();
                try {
                    kafkaTemplate.send("weekly-sentiments", sentimentData.getEmail(), sentimentData);
                } catch (Exception e) {
                    emailService.sendEmail(sentimentData.getEmail() , "Sentiment for previous week" , sentimentData.getSentiment());
                }
            }
        }
    }


    @Scheduled(cron = "0 0/10 * ? * *")     //refreshes the data from DB every ten minutes when running
    public void clearAppCache() {
        appCache.init();
    }
}
