package net.engineeringdigest.journalApp.config;

import net.engineeringdigest.journalApp.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Configuration   //tells this class can also have Bean methods
@EnableWebSecurity
//@Profile("dev")
public class SpringSecurity extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {  //sirf ye sprng sec ku blueprint deta jo bolta konse request ku auth add karna kisu ni karna,user authnticated he bhi toh kya kya access karsakta, ex: only users jinke paas WRITE ka authority he further security chain processing ke liye jate ni oh ni, toh USER with sirf READ authority ye endPoint access ni krskte
        //super.configure(http);
        http.authorizeRequests()
                .antMatchers("/journal/**" , "/user/**").authenticated()   //if the client hits journal end point he first have to be authenticated(cause now we added decurity to that endpoint 'journal'
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll() //if any other request then journal comes then all the users can access them regardless authenticated or not doesn't matter
                .and()
                .httpBasic();   //jisku bhi authentication add karra spring, unku HTTPBasic wala add kar
        //http.csrf().disable(); just only to disable csrf
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf().disable();
    }

    //jab 1st security he endpoint ku, user ku autheticate karne ye padta/call karta spring aur behave karta
    @Override    //ye authentication blueprint likhre/override apan(spring s auth checking acc to isse karta abb), it tells spring s how to verify user's identity
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());  //when user enters username and password, spirng by this auth.userDetailsService(userDetailsService)
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
//for authentication what creds we require for getting authenticated we can configure that by overrding configure(Authen....) method