package com.yedidin.socket.socket_project_last_project.Controller;

import com.yedidin.socket.socket_project_last_project.Entity.Event;
import com.yedidin.socket.socket_project_last_project.Entity.Role;
import com.yedidin.socket.socket_project_last_project.Entity.User;
import com.yedidin.socket.socket_project_last_project.Repository.RoleRepository;
import com.yedidin.socket.socket_project_last_project.Service.EventService;
import com.yedidin.socket.socket_project_last_project.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")

public class AdminController {
    private final UserService userService;
    private final EventService eventService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(UserService userService, EventService eventService,
                           RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.eventService = eventService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // קבלת כל המשתמשים
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // קבלת משתמש לפי ID
    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    // עדכון משתמש
    @PutMapping("users/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User updatedUser, @PathVariable Long id) {
        try {
            User currentUser = userService.getUserById(id);

            // עדכון כל הפרטים
            currentUser.setFirstName(updatedUser.getFirstName());
            currentUser.setLastName(updatedUser.getLastName());
            currentUser.setEmail(updatedUser.getEmail());

            // אם יש תפקיד חדש, מעדכן אותו
            if (updatedUser.getRole() != null) {
                currentUser.setRole(updatedUser.getRole());
            }

            // אם המשתמש שלח סיסמה חדשה, עדכון עם הצפנה
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                if (!passwordEncoder.matches(updatedUser.getPassword(), currentUser.getPassword())) {
                    currentUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }
            }

            // שמירת העדכון
            userService.updateUser(currentUser);
            return ResponseEntity.ok("User updated successfully");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }


    // מחיקת משתמש
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // קבלת כל האירועים
    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }
}