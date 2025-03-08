package com.yedidin.socket.socket_project_last_project.DTO;
public class LoginRequest {
    private String email;
    private String password;

    // Constructor ריק
    public LoginRequest() {
    }

    // Constructor עם פרמטרים
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
