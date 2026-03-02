package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import net.engineeringdigest.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

//@Slf4j
@Component
@Slf4j
public class UserService {

    //private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();  //apply it on raw password

    public void saveEntry(User user) {    //to use when changing in user's journal entry(s) , if used the other saveNewUser one then will encrypt the encrypted password again.
        userRepository.save(user);
    }

    public boolean saveNewUser(User user) {  //only to use when creating new/updating user
//        user.setPassword(passwordEncoder.encode(user.getPassword()));  //returns the encrypted password and we save it
//        user.setRoles(Arrays.asList("USER"));
//        userRepository.save(user);
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));  //returns the encrypted password and we save it
            user.setRoles(Arrays.asList("USER"));
            userRepository.save(user);
            return true;
        }
        catch (DataIntegrityViolationException e) {
//            log.trace("YOYOOYOYOYOYOYOOO");
//            log.debug("YOYOOYOYOYOYOYOOO");
//            log.info("YOYOOYOYOYOYOYOOO");
//            log.warn("YOYOOYOYOYOYOYOOO");
            log.error("Error occured for {}" ,user.getUserName(), e);
            return false;
        }
    }
    public void saveAdmin(User user) {  //only to use when creating new/updating user
        user.setPassword(passwordEncoder.encode(user.getPassword()));  //returns the encrypted password and we save it
        user.setRoles(Arrays.asList("USER" , "ADMIN"));
        userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(ObjectId id) {
        return userRepository.findById(id);
    }

    public void deleteById(ObjectId id) {
        userRepository.deleteById(id);
    }

    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }
}
