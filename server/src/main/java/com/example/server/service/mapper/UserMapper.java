package com.example.server.service.mapper;

import com.example.common.dto.UserDTO;
import com.example.server.domain.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
                user.getId(),
                user.getAzureId(),
                user.getEmail(),
                user.getDisplayName(),
                null,
                user.getRoleId(),
                user.getTeamId(),
                user.getActive(),
                user.getCreatedAt()
        );
    }

    public static User fromDto(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        return new User(
                dto.getId(),
                dto.getAzureId(),
                dto.getEmail(),
                dto.getDisplayName(),
                null,
                dto.getRoleId(),
                dto.getTeamId(),
                dto.getActive(),
                dto.getCreatedAt()
        );
    }
}
