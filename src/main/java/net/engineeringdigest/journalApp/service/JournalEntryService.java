package net.engineeringdigest.journalApp.service;

import ch.qos.logback.core.CoreConstants;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class JournalEntryService {
    @Autowired
    private JournalEntryRepository journalEntryRepository;
    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String userName) {
        try{
            User user = userService.findByUserName(userName);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalEntryRepository.save(journalEntry);//.save() method returns the same entry after saving, directly journalEntry object user ke list ke bhi kyun ni add karsakte ? because id is ObjectId so SB generates it when saving so therefore only after saving in MongoDb use the Object id thatswhy
            user.getJournalEntries().add(saved);
            //user.setUserName(null);
            userService.saveEntry(user);

        } catch(Exception e) {
            System.out.println(e);
            throw new RuntimeException("An error occured while saving the entry." , e);
        }
    }
    //for put mapping
    public void saveEntry(JournalEntry oldEntry , JournalEntry newEntry) {
        oldEntry.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().isEmpty() ? newEntry.getTitle() : oldEntry.getTitle());
        oldEntry.setContent(newEntry.getContent() != null && !newEntry.getContent().isEmpty() ? newEntry.getContent() : oldEntry.getContent());
        journalEntryRepository.save(oldEntry);
    }
    public void saveEntry(JournalEntry entry) {
        journalEntryRepository.save(entry);
    }


    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public boolean deleteById(ObjectId id, String userName){
        boolean removed = false;
        try {
            User user = userService.findByUserName(userName);
            removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
            if(removed) {
                userService.saveEntry(user);
                journalEntryRepository.deleteById(id);
            }
        } catch (Exception e){
           log.error("Error :", e);
            throw new RuntimeException("An error occured while deleting the entry!");
        }
        return removed;
    }
}
