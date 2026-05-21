package net.engineeringdigest.journalApp.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.engineeringdigest.journalApp.cache.AppCache;
import net.engineeringdigest.journalApp.dto.UserRequestDTO;
import net.engineeringdigest.journalApp.dto.UserResponseDTO;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.mapper.UserMapper;
import net.engineeringdigest.journalApp.repository.UserRepository;
import net.engineeringdigest.journalApp.service.UserService;
import org.apache.catalina.core.ApplicationFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin APIs")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppCache appCache;

    @Operation(summary = "get all users information")
    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAll();
        if(users != null && !users.isEmpty()) {
            List<UserResponseDTO> dtoList = new ArrayList<>();
            for(User user : users) {
                dtoList.add(UserMapper.toDTO(user));
            }
//            List<UserResponseDTO> dtoList = users.stream()
//                    .map(UserMapper::toDTO).collect(Collectors.toList());
            return new ResponseEntity<>(dtoList , HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create-admin")   //this method can only be accesed by other admins
    public void createAdmin(@RequestBody UserRequestDTO dto) {
        userService.saveAdmin(UserMapper.toEntity(dto));
    }

    @GetMapping("clear-app-cache")
    public void clearAppCache() {
        appCache.init();
    }

}
