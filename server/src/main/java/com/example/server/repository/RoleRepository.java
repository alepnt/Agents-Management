// Define the repository package to group Spring Data components.
package com.example.server.repository;

// Import the Role entity managed by this repository.
import com.example.server.domain.Role;
// Import CrudRepository to inherit basic CRUD operations.
import org.springframework.data.repository.CrudRepository;
// Import Repository annotation to register the interface as a Spring bean.
import org.springframework.stereotype.Repository;

// Import Optional to wrap possibly absent query results.
import java.util.Optional;

// Mark this interface as a Spring Data repository bean.
@Repository
// Declare the repository for Role entities with Long identifiers.
public interface RoleRepository extends CrudRepository<Role, Long> {

    // Retrieve a role by its name if present.
    Optional<Role> findByName(String name);
}
