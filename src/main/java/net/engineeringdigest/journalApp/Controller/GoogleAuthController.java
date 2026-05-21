package net.engineeringdigest.journalApp.Controller;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.UserDetailsServiceImpl;
import net.engineeringdigest.journalApp.service.UserService;
import net.engineeringdigest.journalApp.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/auth/google")
public class GoogleAuthController {

    @Value("${GOOGLE_CLIENT_ID}")
    private String clientId;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code) {  //we take the authcode from postman
        try {
            //1. exchange Auth code for tokens
            String tokenEndPoint = "https://oauth2.googleapis.com/token";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", "https://developers.google.com/oauthplayground");
            params.add("grant_type", "authorization_code");  //tells what we sending to Auth server(so based on it it sends the correct thing, if here code then it sends back token)

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);  //telling that how we create Appn form  similarly we sending request

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params , headers);

            //exchange auth code with token(make POST req with the 5 main things we learnt)
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndPoint, request, Map.class);
            String idToken = (String) tokenResponse.getBody().get("id_token");
            //now after getting token we we exchanged with auth code -> now get inf needed with the token
            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;   //hit the google's api endpoint which gives us the token
            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);
            if(userInfoResponse.getStatusCode() == HttpStatus.OK) {
                Map<String , Object> userInfo = userInfoResponse.getBody();  //we for scope(in FE/playground) we had put only email so Auth server returned us only email in response for token
                String email = (String) userInfo.get("email");

                //check now in our DB that if a user with this email exists or not if not then we create it
                UserDetails userDetails = null;
                try {
                    userDetails = userDetailsService.loadUserByUsername(email);
                } catch (Exception e) {
                    User user = new User();
                    user.setEmail(email);
                    user.setUserName(email);
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    user.setRoles(Arrays.asList("USER"));
                    userService.saveEntry(user);
                    userDetails = userDetailsService.loadUserByUsername(email);
                }
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                String jwtToken = jwtUtil.generateToken(email);
                return ResponseEntity.ok(Collections.singletonMap("token" , jwtToken));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch (Exception e) {
            log.error("Exception occured while handleGoogleCallback " , e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
