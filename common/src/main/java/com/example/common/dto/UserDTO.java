package com.example.common.dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO per rappresentare un utente applicativo.
 */
public class UserDTO {

    private Long id;
    private String azureId;
    private String email;
    private String displayName;
    private String password;
    private Long roleId;
    private Long teamId;
    private Boolean active;
    private LocalDateTime createdAt;

    public UserDTO() {
    }

    public UserDTO(Long id,
                   String azureId,
                   String email,
                   String displayName,
                   String password,
                   Long roleId,
                   Long teamId,
                   Boolean active,
                   LocalDateTime createdAt) {
        this.id = id;
        this.azureId = azureId;
        this.email = email;
        this.displayName = displayName;
        this.password = password;
        this.roleId = roleId;
        this.teamId = teamId;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAzureId() {
        return azureId;
    }

    public void setAzureId(String azureId) {
        this.azureId = azureId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", azureId='" + azureId + '\'' +
                ", email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                ", roleId=" + roleId +
                ", teamId=" + teamId +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
}
