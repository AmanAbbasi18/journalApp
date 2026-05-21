package net.engineeringdigest.journalApp.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.engineeringdigest.journalApp.dto.UserRequestDTO;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.UserRepository;
import net.engineeringdigest.journalApp.response.WeatherResponse;
import net.engineeringdigest.journalApp.service.JournalEntryService;
import net.engineeringdigest.journalApp.service.UserService;
import net.engineeringdigest.journalApp.service.WeatherService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@Tag(name = "User APIs" , description = "Read, Update & Delete User")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeatherService weatherService;

    //Update an user entry
    @PutMapping
    @Operation(summary = "Update the user")
    public ResponseEntity<Void> updateUser(@RequestBody UserRequestDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();   //if got authenticated then his details are gonna be in SecurityContextHolder like username from login and password(raw) and roles and all from db

        User userInDb = userService.findByUserName(userName);  //since it will save the user with same userId(ObjectId) we don't have to change anything else on same object it will get updated, takes informtn from the auth obj and can update the name and password aswell
//        userInDb.setUserName(dto.getUserName());     //no need to put check because if got authenticated then means exists in DB
//        userInDb.setPassword(dto.getPassword());  //changes password aswell no thatswhy doing savenewUser -> in it doing password encoding else would have used just save user
//        userInDb.setEmail(dto.getEmail());
//        userService.saveNewUser(userInDb);
        userService.updateUser(userInDb , dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUserByUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        userRepository.deleteByUserName(userName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); //204
    }

    @Operation(summary = "Tells you weather and temperature")
    @GetMapping
    public ResponseEntity<?> greetings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        WeatherResponse weatherResponse = weatherService.getWeather("Mumbai");

        String greetings = "";
        if(weatherResponse != null) {
            String weatherFeelslike = "Weather feels like " +  weatherResponse.getCurrent().getFeelslikeC();
            String temp = "Today temp is " + weatherResponse.getCurrent().getTempC();
            greetings = temp + " and " + weatherFeelslike;
        }
        return new ResponseEntity<>("Hi " + greetings , HttpStatus.OK);
    }
}
