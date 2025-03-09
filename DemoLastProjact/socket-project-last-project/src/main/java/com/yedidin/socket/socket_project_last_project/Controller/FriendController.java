package com.yedidin.socket.socket_project_last_project.Controller;

import com.yedidin.socket.socket_project_last_project.DTO.EventRequest;
import com.yedidin.socket.socket_project_last_project.Entity.Event;
import com.yedidin.socket.socket_project_last_project.Entity.User;
import com.yedidin.socket.socket_project_last_project.Repository.UserRepository;
import com.yedidin.socket.socket_project_last_project.Service.EventService;
import com.yedidin.socket.socket_project_last_project.Service.UserService;
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

@RestController
@RequestMapping("/api/friends")
@PreAuthorize("hasRole('FRIEND')")
public class FriendController {


    private final EventService eventService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public FriendController(EventService eventService, UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.eventService = eventService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // קבלת פרטי המשתמש המחובר
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
    // קבלת כל האירועים שהחבר מוקצה אליהם
    @GetMapping("/events")
    public ResponseEntity<?> getAllEvents() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("משתמש לא נמצא");
        }

        // הבא את כל האירועים במערכת במקום רק אלו שמוקצים למשתמש הנוכחי
        List<Event> events = eventService.findAllEvents();
        System.out.println("נמצאו " + events.size() + " אירועים במערכת");

        if (events.isEmpty()) {
            return ResponseEntity.ok("לא נמצאו אירועים במערכת");
        }
        return ResponseEntity.ok(events);
    }

    // קבלת אירוע ספציפי
    @GetMapping("/events/{eventId}")
    public ResponseEntity<?> getEvent(@PathVariable Long eventId) {
        User currentUser = userService.getCurrentUser();
        Event event = eventService.getEventById(eventId);

        // בדיקה שהאירוע מוקצה לחבר הנוכחי
        if (event.getFriend() == null || !event.getFriend().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("אין לך הרשאה לצפות באירוע זה");
        }

        return ResponseEntity.ok(event);
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
    // עדכון סטטוס של אירוע
    @PatchMapping("/events/{eventId}/status")
    public ResponseEntity<?> updateEventStatus(
            @PathVariable Long eventId,
            @RequestBody Map<String, String> statusUpdate) {

        User currentUser = userService.getCurrentUser();
        Event event = eventService.getEventById(eventId);

        // 📌 לוגים חשובים
        System.out.println("🔹 User ID: " + currentUser.getId());
        System.out.println("🔹 User Role: " + currentUser.getRole());
        System.out.println("🔹 Event Friend ID: " + (event.getFriend() != null ? event.getFriend().getId() : "NULL"));

        // בדיקה שהאירוע מוקצה לחבר הנוכחי
        if (event.getFriend() == null || !event.getFriend().getId().equals(currentUser.getId())) {
            event.setFriend(currentUser);
        }
        if (!event.getFriend().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("⛔ אין לך הרשאה לעדכן אירוע זה");
        }
        if (statusUpdate.containsKey("isDone")) {
            event.setIsDone(statusUpdate.get("isDone"));
            eventService.saveEvent(event);
            System.out.println("✅ סטטוס האירוע עודכן בהצלחה!");
            return ResponseEntity.ok("סטטוס האירוע עודכן בהצלחה");
        } else {
            System.out.println("⚠️ שגיאה: חובה לציין סטטוס");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("חובה לציין סטטוס");
        }
    }

    @PutMapping("/events/{eventId}")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long eventId,
            @RequestBody Map<String, String> statusUpdate) {

        User currentUser = userService.getCurrentUser();
        Event event = eventService.getEventById(eventId);

        // בדיקה שהאירוע מוקצה לחבר הנוכחי
        if (event.getFriend() == null || !event.getFriend().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("אין לך הרשאה לעדכן אירוע זה");
        }

        if (statusUpdate.containsKey("isDone")) {
            event.setIsDone(statusUpdate.get("isDone"));
            eventService.saveEvent(event);
            return ResponseEntity.ok("סטטוס האירוע עודכן בהצלחה");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("חובה לציין סטטוס");
        }
    }
    @PostMapping("/events/{eventId}/assign")
    public ResponseEntity<?> assignEventToCurrentFriend(@PathVariable Long eventId) {
        User currentUser = userService.getCurrentUser();

        // בדיקה שהמשתמש קיים
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("משתמש לא נמצא");
        }

        try {
            // קבלת האירוע
            Event event = eventService.getEventById(eventId);

            // בדיקה שהאירוע לא משויך כבר לחבר אחר
            if (event.getFriend() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("האירוע כבר משויך לחבר אחר");
            }

            // שיוך האירוע לחבר הנוכחי
            event.setFriend(currentUser);
            event.setIsDone("no"); // ברירת מחדל - בטיפול

            // שמירת האירוע
            eventService.saveEvent(event);

            return ResponseEntity.ok("האירוע שויך בהצלחה");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("שגיאה בשיוך האירוע: " + e.getMessage());
        }
    }




        @PatchMapping("/{eventId}")
        public ResponseEntity<?> assignFriendToEvent(@PathVariable Long eventId, @RequestBody Map<String, Long> request) {
            try {
                Long friendId = request.get("friendId");
                eventService.assignFriendToEvent(eventId, friendId);
                return ResponseEntity.ok("Friend assigned successfully to event");
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event or friend not found");
            }
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
}
