package net.engineeringdigest.journalApp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotNull;

//used for input from client, for when the user signup, login then he shouldn't be able to touch/anything to do with JE and Roles(cause by-default his role is set to 'USER' and weknow only admin can make someone admin
@Data
@Builder
public class UserRequestDTO {  //because client should only send this, cuase can't let a client play/update with roles[] and journalEntries, Where? /signup, /login, /user update
    @Indexed(unique = true)
    @Schema(description = "The user's username")
    private String userName;
    @NotNull
    private String password;
    private String email;
    private boolean sentimentAnalysis;
}
