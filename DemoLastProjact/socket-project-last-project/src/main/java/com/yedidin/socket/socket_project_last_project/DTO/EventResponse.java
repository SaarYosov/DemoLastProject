package com.yedidin.socket.socket_project_last_project.DTO;

import java.time.LocalDateTime;

public class EventResponse {
    private Long id;
    private String userFirstName;
    private String userLastName;
    private String friendFirstName;
    private String friendLastName;
    private double latitude;
    private double longitude;
    private String eventDescription;
    private String isDone;
    private String priority;
    private LocalDateTime date;

    public EventResponse(Long id, String userFirstName, String userLastName, String friendFirstName, String friendLastName,
                         double latitude, double longitude, String eventDescription, String isDone, String priority, LocalDateTime date) {
        this.id = id;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.friendFirstName = friendFirstName;
        this.friendLastName = friendLastName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.eventDescription = eventDescription;
        this.isDone = isDone;
        this.priority = priority;
        this.date = date;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public String getFriendFirstName() {
        return friendFirstName;
    }

    public String getFriendLastName() {
        return friendLastName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getIsDone() {
        return isDone;
    }

    public String getPriority() {
        return priority;
    }

    public LocalDateTime getDate() {
        return date;
    }
}