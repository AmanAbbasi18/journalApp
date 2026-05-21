package net.engineeringdigest.journalApp.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginRequestDTO {
    private String userName;
    private String password;

}
