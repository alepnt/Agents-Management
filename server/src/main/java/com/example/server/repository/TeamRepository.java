// Define the package for repository interfaces.
package com.example.server.repository;

// Import the Team entity managed by this repository.
import com.example.server.domain.Team;
// Import CrudRepository to access standard CRUD operations.
import org.springframework.data.repository.CrudRepository;
// Import Repository annotation to register the interface with Spring.
import org.springframework.stereotype.Repository;

// Import Optional to represent potentially missing results.
import java.util.List;
import java.util.Optional;

// Mark this interface as a Spring Data repository bean.
@Repository
// Declare the repository for Team entities with Long identifiers.
public interface TeamRepository extends CrudRepository<Team, Long> {

    // Retrieve a team by its name if present.
    @org.springframework.data.jdbc.repository.query.Query("SELECT id, name FROM teams WHERE name = :name")
    Optional<Team> findByName(String name);

    List<Team> findAllByOrderByNameAsc();
}
