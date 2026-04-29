package net.engineeringdigest.journalApp.filter;

import net.engineeringdigest.journalApp.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization"); //will get auth header
        String username = null;
        String jwt = null;
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {  //if the authr header is indeed Jwt token(cause Jwt token in auth header starts with Bearer)
            jwt = authorizationHeader.substring(7);
            username  = jwtUtil.extractUsername(jwt);    //then extract username
        }
        if(username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);  //fetched user details from db
            if(jwtUtil.validateToken(jwt)) {  //after extracting username from jwt token check if the token isn't expired yet to perform this operation
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));  //we are manually forming the authManager were we directly used to use when we used Basic auth ,here we're creating ourselves and setting in the security context(jab ki default jab basic auth use karre the udhar login page ka info context me store hota tha aur security Context holder ke andar
                SecurityContextHolder.getContext().setAuthentication(auth); //is condn me tabhi ayega banda jab authncte hogaya hoga
            }
        }
        response.addHeader("admin" , "Rohit");  //can skip it its just metadata(extra info) for the requested user
        chain.doFilter(request , response);    //resquest aur response hamne aage ke filters ke liye bhejdiya agr he toh
    }
}
