package net.engineeringdigest.journalApp.Controller;

import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
public class publicController {
    @Autowired
    private UserService userService;

    //get also public
    //get all the user entries
    @GetMapping("/health-check")
    public ResponseEntity<?> getAllUsers() {
        List<User> allUsers = userService.getAll();
        //System.out.println(allUsers.toString());
        if(allUsers != null && !allUsers.isEmpty()) {
            return new ResponseEntity<>(allUsers , HttpStatus.OK);  //200
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);   //404
    }

    //create a new user entry
    @PostMapping("/create-user")
    public ResponseEntity<Void> createUser(@RequestBody User user) {  //jo bhi client bhejta body me json to java corresponding object me convert hojata,(body bhejre so aur hamare obj ke jitte bhi fields wan se bhejre (max <= our no. of fields) unke field name same rehna
        if(userService.saveNewUser(user)){
            return new ResponseEntity<>(HttpStatus.CREATED);   //201
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }
    }
}
