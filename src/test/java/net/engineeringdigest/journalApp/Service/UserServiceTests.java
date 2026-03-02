package net.engineeringdigest.journalApp.Service;

import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.UserRepository;
import net.engineeringdigest.journalApp.service.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Disabled
//@ActiveProfiles("prod")
public class UserServiceTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Disabled
    @ParameterizedTest
//    @ValueSource(strings = {
//            "Ram",
//            "Rohit",
//            "Akshit"
//    })
    @ArgumentsSource(UserArguentsProvider.class)
    public void testSaveNewUser(User user) {
        assertTrue(userService.saveNewUser(user));
    }

    @ParameterizedTest //Because it avoids duplicate code and allows testing multiple inputs efficiently in a single test method.
    @CsvSource({   //giving multiple inputs and edgecases to validate the methods is working fine
            "1, 1, 2",
            "2, 10, 12",
            "3, 3, 6"
    })
    public void test(int a, int b, int expected) {
        assertEquals(expected , a+b);
    }
}
