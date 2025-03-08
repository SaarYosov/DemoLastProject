package com.yedidin.socket.socket_project_last_project.Repository;

import com.yedidin.socket.socket_project_last_project.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
//
//    // Custom query to find a role by its name
//    Optional<Role> findById(long RoleId);
Optional<Role> findByRoleName(String roleName);
}
