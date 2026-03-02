package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override    //with the username we are fetching that user obj, and if couldn't fetch then throw exception doesn't exist karke, if exists then cannot just directly return it in the same format needed in
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {  //loading user through using username
        User user = userRepository.findByUserName(username);
        if(user != null) {
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUserName())
                    .password(user.getPassword())
                    .roles(user.getRoles().toArray(new String[0]))  //then see in its implentn Roles it need it seperated by , so turn it into array of string
                    .build();
            return userDetails;    //retruning 1 ex is to the spring security internally spring calls it to compare while authenticating
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
