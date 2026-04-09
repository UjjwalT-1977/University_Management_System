package com.university.dto;

/**
 * A temporary object to catch the incoming JSON from React
 */
public class LoginRequest {
    private String identifier; // This will be the username, roll number, or emp ID
    private String password;
    private String role;       // "ADMIN", "STUDENT", or "FACULTY"

    // Getters and Setters
    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
