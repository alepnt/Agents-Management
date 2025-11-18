package com.example.server.service.mapper;

import com.example.common.dto.TeamDTO;
import com.example.server.domain.Team;

public final class TeamMapper {

    private TeamMapper() {
    }

    public static TeamDTO toDto(Team team) {
        if (team == null) {
            return null;
        }
        return new TeamDTO(
                team.getId(),
                team.getName()
        );
    }

    public static Team fromDto(TeamDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Team(
                dto.getId(),
                dto.getName()
        );
    }
}
