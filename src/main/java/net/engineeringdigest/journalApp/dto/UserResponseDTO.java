package net.engineeringdigest.journalApp.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UserResponseDTO { //Used for OUTPUT to client, where? /admin/all-users ,/user profile (if you add later)
    private String userName;
    private String email;
    private boolean sentimentAnalysis;
}
