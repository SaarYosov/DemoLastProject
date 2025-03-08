package com.yedidin.socket.socket_project_last_project.Service;

import com.yedidin.socket.socket_project_last_project.Entity.Role;

import java.util.Optional;

public interface IRoleService {

    Optional<Role> getRole(long roleId);

}
