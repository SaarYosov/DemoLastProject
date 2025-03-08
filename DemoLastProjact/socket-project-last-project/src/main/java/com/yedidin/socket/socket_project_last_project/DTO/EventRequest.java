package com.yedidin.socket.socket_project_last_project.DTO;


import java.math.BigDecimal;

public class EventRequest {
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String eventDescription;
    private String priority; // HIGH, MEDIUM, LOW

    public EventRequest() {
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}