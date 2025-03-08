package com.yedidin.socket.socket_project_last_project.Controller;


import com.yedidin.socket.socket_project_last_project.Entity.Event;
import com.yedidin.socket.socket_project_last_project.Entity.User;
import com.yedidin.socket.socket_project_last_project.Service.EventService;
import com.yedidin.socket.socket_project_last_project.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
public class EventController {


    private final EventService eventService;
    private final UserService userService;

    @Autowired
    public EventController (EventService eventService, UserService  userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping
    public List<Event> getAllEvents(){
        return eventService.getEventsForUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        try {
            Event event = eventService.getEventById(id);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        return ResponseEntity.ok(eventService.createEvent(event));
    }


    // עדכון אירוע קיים
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Event eventDetails) {
        try {
            Event existingEvent = eventService.getEventById(id);
            User currentUser = userService.getCurrentUser();

            // בדיקת הרשאות - רק יוצר האירוע או מנהל יכולים לעדכן
            if (!existingEvent.getUser().getId().equals(currentUser.getId()) &&
                    !currentUser.getRole().getRoleName().equals("ROLE_ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to edit this event");
            }

            // עדכון השדות
            if (eventDetails.getLatitude() != null) {
                existingEvent.setLatitude(eventDetails.getLatitude());
            }

            if (eventDetails.getLongitude() != null) {
                existingEvent.setLongitude(eventDetails.getLongitude());
            }

            if (eventDetails.getEventDescription() != null) {
                existingEvent.setEventDescription(eventDetails.getEventDescription());
            }

            if (eventDetails.getIsDone() != null) {
                existingEvent.setIsDone(eventDetails.getIsDone());
            }

            if (eventDetails.getPriority() != null) {
                existingEvent.setPriority(eventDetails.getPriority());
            }

            // עדכון חבר אם יש
            if (eventDetails.getFriend() != null && eventDetails.getFriend().getId() != null) {
                User friend = userService.getUserById(eventDetails.getFriend().getId());
                existingEvent.setFriend(friend);
            }

            Event updatedEvent = eventService.createEvent(existingEvent);
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // עדכון סטטוס אירוע (עבור חברים)
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateEventStatus(@PathVariable Long id, @RequestBody Event statusUpdate) {
        try {
            Event existingEvent = eventService.getEventById(id);
            User currentUser = userService.getCurrentUser();

            // בדיקת הרשאות - רק החבר שהוקצה לאירוע יכול לעדכן סטטוס
            if (existingEvent.getFriend() == null || !existingEvent.getFriend().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("אין לך הרשאה לעדכן סטטוס אירוע זה");
            }

            // עדכון סטטוס אירוע
            if (statusUpdate.getIsDone() != null) {
                existingEvent.setIsDone(statusUpdate.getIsDone());
                Event updatedEvent = eventService.createEvent(existingEvent);
                return ResponseEntity.ok(updatedEvent);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("חובה לציין סטטוס");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvents(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    // קבלת אירועים לפי ID של משתמש
    @GetMapping("/user/{userId}")
    public List<Event> getEventsByUserId(@PathVariable Long userId) {
        return eventService.findByUserId(userId);
    }

    // קבלת אירועים לפי ID של חבר
    @GetMapping("/friend/{friendId}")
    public List<Event> getEventsByFriendId(@PathVariable Long friendId) {
        return eventService.findByFriendId(friendId);
    }
}
