// Declare the package containing repository interfaces.
package com.example.server.repository;

// Import the User entity to define repository operations.
import com.example.server.domain.User;
// Import CrudRepository to expose CRUD operations for users.
import org.springframework.data.repository.CrudRepository;
// Import Repository annotation to register the interface with Spring.
import org.springframework.stereotype.Repository;

// Import List to return ordered collections of users.
import java.util.List;
// Import Optional to wrap queries that may not find a result.
import java.util.Optional;

// Mark this interface as a Spring Data repository bean.
@Repository
// Declare the repository for User entities with Long identifiers.
public interface UserRepository extends CrudRepository<User, Long> {

    // Retrieve all users sorted by their display name in ascending order.
    List<User> findAllByOrderByDisplayNameAsc();

    // Find a user by email if present.
    Optional<User> findByEmail(String email);

    // Find a user by Azure AD identifier if present.
    Optional<User> findByAzureId(String azureId);

    // Retrieve all users belonging to a specific team.
    List<User> findByTeamId(Long teamId);
}
