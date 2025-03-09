package com.yedidin.socket.socket_project_last_project.Controller;

import com.yedidin.socket.socket_project_last_project.DTO.EventRequest;
import com.yedidin.socket.socket_project_last_project.DTO.EventResponse;
import com.yedidin.socket.socket_project_last_project.Entity.Event;
import com.yedidin.socket.socket_project_last_project.Entity.User;
import com.yedidin.socket.socket_project_last_project.Repository.EventRepository;
import com.yedidin.socket.socket_project_last_project.Repository.UserRepository;
import com.yedidin.socket.socket_project_last_project.Service.EventService;
import com.yedidin.socket.socket_project_last_project.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;
    @Autowired
    private UserRepository userRepository;
@Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping("/me")
    public ResponseEntity<?> getUserProfile() {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(Map.of(
                "id", currentUser.getId(),
                "firstName", currentUser.getFirstName(),
                "lastName", currentUser.getLastName(),
                "email", currentUser.getEmail()
        ));
    }
//    // קבלת משתמש לפי ID
@GetMapping("/{userId}")
public ResponseEntity<?> getUser(@PathVariable Long userId) {
    // קבל את המידע מה-JWT של המשתמש המחובר
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName(); // ה-Email מהטוקן
    User loggedInUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // בדיקה אם המשתמש מנסה לגשת למידע שלו או שהוא אדמין
    if (!loggedInUser.getId().equals(userId) && !loggedInUser.getRole().getRoleName().equals("ROLE_ADMIN")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to view this user");
    }

    return ResponseEntity.ok(loggedInUser);
}

    @PutMapping("/{id}")
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


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        // קבל את המשתמש המחובר מהטוקן
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User loggedInUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // אם המשתמש לא מחובר לעצמו ולא אדמין – חוסמים אותו
        if (!loggedInUser.getId().equals(id) && !loggedInUser.getRole().getRoleName().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this user");
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/events")
    public ResponseEntity<?> createEvent(@RequestBody EventRequest eventRequest) {
        // Get logged-in user from JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create new event
        Event event = new Event();
        event.setUser(user);
        event.setLatitude(eventRequest.getLatitude());
        event.setLongitude(eventRequest.getLongitude());
        event.setEventDescription(eventRequest.getEventDescription());
        event.setPriority(eventRequest.getPriority());
        event.setDate(LocalDateTime.now()); // Set current time

        // Save event
        Event savedEvent = eventService.createEvent(event);

        return ResponseEntity.ok(savedEvent);
    }
@GetMapping("/events")
public ResponseEntity<List<EventResponse>> getUserEvents() {
    // קבל את המשתמש המחובר מהטוקן
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    User loggedInUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // שליפת כל האירועים של המשתמש המחובר
    List<Event> userEvents = eventService.findByUserId(loggedInUser.getId());

    // המרת `Event` ל-`EventResponse` כדי להחזיר רק את הנתונים הרלוונטיים
    List<EventResponse> eventResponses = userEvents.stream()
            .map(event -> new EventResponse(
                    event.getId(),
                    event.getUser().getFirstName(),
                    event.getUser().getLastName(),
                    event.getFriend() != null ? event.getFriend().getFirstName() : null,
                    event.getFriend() != null ? event.getFriend().getLastName() : null,
                    event.getLatitude().doubleValue(),  // המרת BigDecimal ל-double
                    event.getLongitude().doubleValue(), // המרת BigDecimal ל-double
                    event.getEventDescription(),
                    event.getIsDone(),
                    event.getPriority(),
                    event.getDate()
            ))
            .collect(Collectors.toList());

    return ResponseEntity.ok(eventResponses);
}
}
