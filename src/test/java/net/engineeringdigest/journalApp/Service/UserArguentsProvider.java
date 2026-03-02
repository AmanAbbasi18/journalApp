package net.engineeringdigest.journalApp.Service;

import net.engineeringdigest.journalApp.entity.User;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;


import java.util.stream.Stream;

public class UserArguentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Stream.of(   //retruning stream of users
                Arguments.of(User.builder().userName("shyam").password("shyam").build()),
                Arguments.of(User.builder().userName("suraj").password("").build())
        );
    }
}
