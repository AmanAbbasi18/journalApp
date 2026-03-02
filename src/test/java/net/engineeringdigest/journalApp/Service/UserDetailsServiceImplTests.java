package net.engineeringdigest.journalApp.Service;

import net.bytebuddy.asm.Advice;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.UserRepository;
import net.engineeringdigest.journalApp.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

//@SpringBootTest   //same as @SpringBootApplication but for tests(has also inside -> @Component), so when removed Autowired doesn't work.
@Disabled
public class UserDetailsServiceImplTests {

//    @Autowired
//    private UserDetailsServiceImpl userDetailsService;
//    ham idhar bean chahre the aur since usme depency bhi lagri thi toh uske liye ham @MockBean use karre the, lekin still slow thatswhy rmeove it and doing it withour need of TestAppContext so now using InjectMocks which injects mock(whichever depepncy is marked @Mock tose will be injected in obj/bean mearked with @InjectMocks) in the curr Bean
//    @MockBean     //@Mock wala istemaal karenge toh ni work karega aur UserDetailsServiceImpl ke class me loadByUse...() method me 1st line me userRepo se ich data load hota naki mock obj milta ,why? because @Mock is not related when TestApplicationContext is there(meaning class marked with @SpringBootTest ,so testAppContext to save it in IOC container it need a Bean so thatswhy have to mark depenceny with @MockBean to get a fake/dummy bean or else gets actually from DB .now the fake one/bean gets injected when spring sees the userService here needs an dependency
//    private UserRepository userRepository;   //we are taking a mockBean because userDetailsService needs a dependency of repo ,but we don't want to load it from the DB (too much time and unnecessary ,we just need a random obj to test the code out) thatswhy giving a fake dependency to service because when tries to load the user -> the mocked one gives a fake dummy obj

    @InjectMocks    //Qn may arise, but how is this not null(who is instantiating this?) ,@InjectMocks is field ku automatic initialize kardeta ,aur dhundta iske dependencies(mocks) inject isme karne
    private UserDetailsServiceImpl userDetailsService;

    @Mock   //(replacement of actual depency)
    private UserRepository userRepository;    //if the @BeforeEach method not present then it will not et intilized and will give nullPointerException (when it will try using userRepo(null) in the UserDetailsService class ,thatswhy)

    @BeforeEach
    void setUp() {  //here only we initialize the mocks    ,bina iske bhi mocks inject hote lekin null inject hote aur jab udhar UserDetailsClass me use karte toh null pe use karte isliye NullPoint..Exceptionlslslsl
        MockitoAnnotations.initMocks(this);    //means initilizeMocks ,which one(s) ? 'this' means all mocks marked ones in this class
    }

    @Test
    void loadUserByUsernameTest() {
        when(userRepository.findByUserName(ArgumentMatchers.anyString()))  //matlab kuch bhi username/string se loadkarna chahta he toh dummy obj dedo harbar
                .thenReturn(User.builder().userName("ram").password("random").roles(new ArrayList<>()).build());     //kya hora boleto jab userService me call hota ye method ,usme ham userRepo.. use karre na jab wo line pe ayenga dekhta ki hame toh uk=ska mocj=kBean banaye so he toh DB se ni load karko ye dummy ka ich dedeta(when() identify karleta jab uss class me wo userRepo... ki line hit karti)
        UserDetails user = userDetailsService.loadUserByUsername("ram");
        Assertions.assertNotNull(user);
    }
}
