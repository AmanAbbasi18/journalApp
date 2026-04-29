package net.engineeringdigest.journalApp.Controller;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.UserDetailsServiceImpl;
import net.engineeringdigest.journalApp.service.UserService;
import net.engineeringdigest.journalApp.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/public")
public class publicController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

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
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody User user) {  //jo bhi client bhejta body me json to java corresponding object me convert hojata,(body bhejre so aur hamare obj ke jitte bhi fields wan se bhejre (max <= our no. of fields) unke field name same rehna
        if(userService.saveNewUser(user)){
            return new ResponseEntity<>(HttpStatus.CREATED);   //201
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {  //jo bhi client bhejta body me json to java corresponding object me convert hojata,(body bhejre so aur hamare obj ke jitte bhi fields wan se bhejre (max <= our no. of fields) unke field name same rehna
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));   //internally userDetailsServieImpl method call hora j ki load karta user ke details DB se
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());  //dah! the fact got autenticated means username and passwrd exists correctly in db
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);   //once he signup and 1st time logins then he gets a jwt token
        }
        catch (Exception e) {     //exception comes from (userDetailsServiceImpl -> springSecurity -> this method) if username doesn't found in db or ( springSecurity -> this method) if password doesn't match
            log.error("Exception occured while createAuthenticationToken ", e);
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }
    }
}
