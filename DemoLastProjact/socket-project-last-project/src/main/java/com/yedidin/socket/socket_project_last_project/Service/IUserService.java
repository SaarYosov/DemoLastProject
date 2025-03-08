package com.yedidin.socket.socket_project_last_project.Service;

import com.yedidin.socket.socket_project_last_project.Entity.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    List<User> getAllUsers();
    Optional<User> findUserById(Long id);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);
    List<User> getUsersByRoleId(Long roleId);
    Optional<User> getUserByEmail(String email);
    User getCurrentUser();
    User getUserById(Long id);
}
