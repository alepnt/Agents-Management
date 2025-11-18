package com.example.common.api;

import com.example.common.dto.UserDTO;

import java.util.List;
import java.util.Optional;

/**
 * Contratto API condiviso per la gestione degli utenti applicativi.
 */
public interface UserApiContract {

    List<UserDTO> listUsers();

    Optional<UserDTO> findById(Long id);

    UserDTO create(UserDTO user);

    UserDTO update(Long id, UserDTO user);

    void delete(Long id);
}
