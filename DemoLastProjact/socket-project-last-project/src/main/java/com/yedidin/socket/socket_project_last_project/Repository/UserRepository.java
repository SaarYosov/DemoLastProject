package com.yedidin.socket.socket_project_last_project.Repository;

import com.yedidin.socket.socket_project_last_project.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query to find a user by email
    Optional<User> findByEmail(String email);

    // Custom query to find a user by first name and last name (example use case)
    Optional<User> findByFirstNameAndLastName(String firstName, String lastName);

    // Custom method to delete a user by their ID
    void deleteById(Long id);

    // Custom method to delete a user by their email
    void deleteByEmail(String email);

    List<User> findByRole_Id(Long roleId);

    boolean existsByEmail(String email);
}