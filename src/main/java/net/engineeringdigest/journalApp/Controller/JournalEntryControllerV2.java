package net.engineeringdigest.journalApp.Controller;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.JournalEntryService;
import net.engineeringdigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerV2 {
    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllEntriesOfUser() {  //localhost:8080/journal GET
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();  //stores login info
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);      //since authenticated ,it means definetly that user exists in DB
        List<JournalEntry> allEntries = user.getJournalEntries();
        if(allEntries != null && !allEntries.isEmpty())
            return new ResponseEntity<>(allEntries , HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry entry) { //localhost:8080/journal POST   //no more taking username from path variable unsafe, use authorization obj saves in itself the login info
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();  //stores login info
            String userName = authentication.getName();
            journalEntryService.saveEntry(entry , userName);  //not inside the userserive class for this ,saveNewUser cause then it would encrypt the already encrypted password ,
            return new ResponseEntity<>(entry , HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/id/{myId}")    //logined user ke paas isa hoskata ki kisi aur ki journal entry ki id milgyi aur dekhna chata he,lekin na bhai isa ni hone denge chalu insan
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId) { //gives journal entry, not user id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();  //stores login info
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());//basically what we are doin is since authenticated we are fetching the user through username and we are traversing the journalentries list of the user and seeing if the client really gave the correct id by checking the user's id with given id or bluffing and trying to get someone's else(sneaky!guy) ,if matched/found then collect the JournalEntry ,So we are restricting the user logined to only fetched entry(s) of his own, we do that by try searching for the given journal entry id in his list of journal entries, if matched he is a good boy
        if(!collect.isEmpty()) {   //means the user logined is trying to fetch his journal entry only, cause given journal entry id matched in his journalEntries list
            Optional<JournalEntry> journalEntry = journalEntryService.findById(myId);
            if(journalEntry.isPresent()) {  //meaning if is not null  //no need of this check because the fact that we entered this condition means that the entry exists in his record only
                return new ResponseEntity<>(journalEntry.get() , HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/id/{id}")   //check fisrt whether the jentry is user's own entry only and not deleting someone else's, so fetch user first from username and traverse through his's Jentries and see if found(he is honest boy) delete it
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();  //stores login info
        String userName = authentication.getName();
        if(journalEntryService.deleteById(id, userName))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/id/{myId}")
    public ResponseEntity<JournalEntry> updateJournalEntryById(@PathVariable ObjectId myId ,
                                                               @RequestBody JournalEntry newEntry) {//username authentication ke dauran kaam ayega
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());
        if(!collect.isEmpty()) {
            Optional<JournalEntry> oldEntry = journalEntryService.findById(myId);  //no need of the below if and putting it inside optional cause forsure we know that since that collect is not null then is present in it
            if(oldEntry.isPresent()) {  //meaning if is not null  //no need of this check because the fact that we entered this condition means that the entry exists in his record only
                journalEntryService.saveEntry(oldEntry.get(), newEntry);
                return new ResponseEntity<>(oldEntry.get(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

//        JournalEntry oldEntry = journalEntryService.findById(myId).orElse(null);
//        if(oldEntry != null) {
//            journalEntryService.saveEntry(oldEntry , newEntry);  //not to use saveNewUser inside UserService class for this, saveEntry of userService is correct 1 is only correct for this
//            return new ResponseEntity<>(oldEntry , HttpStatus.OK);
//        }
//
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
