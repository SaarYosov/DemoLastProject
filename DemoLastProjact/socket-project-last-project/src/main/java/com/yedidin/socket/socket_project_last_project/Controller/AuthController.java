package com.yedidin.socket.socket_project_last_project.Controller;


import com.yedidin.socket.socket_project_last_project.DTO.UserResponse;
import com.yedidin.socket.socket_project_last_project.Entity.Role;
import com.yedidin.socket.socket_project_last_project.Entity.User;
import com.yedidin.socket.socket_project_last_project.DTO.LoginRequest;
import com.yedidin.socket.socket_project_last_project.DTO.RegisterRequest;
import com.yedidin.socket.socket_project_last_project.Repository.RoleRepository;
import com.yedidin.socket.socket_project_last_project.Repository.UserRepository;
import com.yedidin.socket.socket_project_last_project.config.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    @Autowired
    public AuthController(UserRepository userRepository, RoleRepository roleRepository, RoleRepository roleRepository1, PasswordEncoder passwordEncoder, JWTUtils jwtUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository1;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }



    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // מסירים את "Bearer "
        String email = jwtUtils.extractByEmail(jwt); // מפענחים את האימייל מה-JWT

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // מציאת שם התפקיד לפי ה-role_id של המשתמש
        Role role = roleRepository.findById(user.getRole().getId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        return ResponseEntity.ok(new UserResponse(user.getId(), user.getFirstName(), user.getLastName() , user.getEmail(), role.getRoleName()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // בדיקת קיום המשתמש והסיסמה
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new RuntimeException("Wrong password");
            }

            // יצירת טוקן עם התפקיד
            String token = jwtUtils.generateToken(user.getEmail(), user.getRole().getRoleName());

            // מענה עם הטוקן
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("id", user.getId());
            response.put("email", user.getEmail());
            response.put("role", user.getRole().getRoleName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("This email is already in use");
        }

        // מציאת תפקיד USER כברירת מחדל
        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("תפקיד ברירת המחדל לא נמצא."));

        // יצירת משתמש חדש
        User user = new User(
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                userRole
        );

        userRepository.save(user);
        return ResponseEntity.ok("Registration succeed!");
    }
}