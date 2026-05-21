package net.engineeringdigest.journalApp.mapper;

import lombok.Builder;
import net.engineeringdigest.journalApp.dto.UserRequestDTO;
import net.engineeringdigest.journalApp.dto.UserResponseDTO;
import net.engineeringdigest.journalApp.entity.User;

//Must be able t convert DTO -> entity, entity -> DTO

public class UserMapper {
    public static User toEntity(UserRequestDTO dto) {
        return User.builder()
                .userName(dto.getUserName())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .sentimentAnalysis(dto.isSentimentAnalysis())
                .build();
    }

    //especially needed for Admim to send response
    public static UserResponseDTO toDTO(User user) {
        return UserResponseDTO.builder()
                .userName(user.getUserName())
                .email(user.getEmail())
                .sentimentAnalysis(user.isSentimentAnalysis())
                .build();
    }
}
