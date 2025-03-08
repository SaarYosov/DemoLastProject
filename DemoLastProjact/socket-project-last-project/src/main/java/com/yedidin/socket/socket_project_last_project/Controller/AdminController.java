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
    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody Map<String, String> updates) {
        User user = userService.getUserById(userId);

        if (updates.containsKey("firstName")) {
            user.setFirstName(updates.get("firstName"));
        }
        if (updates.containsKey("lastName")) {
            user.setLastName(updates.get("lastName"));
        }
        if (updates.containsKey("email")) {
            user.setEmail(updates.get("email"));
        }

        // ✅ בדיקה אם צריך לעדכן סיסמה
        if (updates.containsKey("currentPassword") && updates.containsKey("password")) {
            String currentPassword = updates.get("currentPassword");
            String newPassword = updates.get("password");

            // בדיקה אם הסיסמה הישנה נכונה
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("סיסמה נוכחית שגויה!");
            }

            // קידוד ושמירה של הסיסמה החדשה
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        if (updates.containsKey("roleId")) {
            Role role = roleRepository.findById(Long.parseLong(updates.get("roleId")))
                    .orElseThrow(() -> new RuntimeException("התפקיד לא נמצא"));
            user.setRole(role);
        }

        userService.updateUser(user);
        return ResponseEntity.ok("המשתמש עודכן בהצלחה");
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