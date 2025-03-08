package com.yedidin.socket.socket_project_last_project.Service;

import com.yedidin.socket.socket_project_last_project.Entity.Event;

import java.util.List;

public interface IEventService {

    Event createEvent(Event event);
    Event updateEvent(Long id ,Event event);
    void deleteEvent(long EventId);
    Event getEventById(long EventId);
    List<Event> findByUserId(Long userId);
    List<Event> findByFriendId(Long friendId);
    List<Event> getAllEvents();
    Event saveEvent(Event event);
}

