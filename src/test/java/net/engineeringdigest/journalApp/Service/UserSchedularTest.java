package net.engineeringdigest.journalApp.Service;

import net.engineeringdigest.journalApp.schedular.UserSchedular;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserSchedularTest {

    @Autowired
    private UserSchedular userSchedular;

    @Test
    public void testfetchUsersAndSendSaMail() {
        userSchedular.fetchUsersAndSendSaMail();
    }
}
