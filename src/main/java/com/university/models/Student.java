package com.university.models;

import java.time.LocalDateTime;

/**
 * Student Entity Class
 * Represents a student in the university system
 * Maps to 'student' table in database
 */
public class Student {
    private int studentId;           // Primary Key
    private String rollNumber;        // Unique roll number
    private String name;             // Full name
    private String email;            // Email address (unique)
    private String phone;            // Phone number
    private LocalDateTime dateOfBirth;   // Date of birth
    private String gender;           // Male/Female/Other
    private int deptId;              // Foreign Key to Department
    private String password;         // Login password
    private LocalDateTime admissionDate; // Admission date
    private int semester;            // Current semester
    private String status;           // Active/Inactive/Graduated
    private double cgpa;             // Cumulative GPA
    private LocalDateTime createdAt;     // Record creation timestamp

    // ==================== CONSTRUCTORS ====================

    /**
     * Default Constructor (Empty)
     * Used when creating empty Student objects
     */
    public Student() {}

    /**
     * Constructor with main fields
     * Used when creating basic student info
     */
    public Student(String rollNumber, String name, String email, int deptId, LocalDateTime admissionDate) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.email = email;
        this.deptId = deptId;
        this.admissionDate = admissionDate;
    }

    /**
     * Full Constructor
     * Used when loading complete student data from database
     */
    public Student(int studentId, String rollNumber, String name, String email, String phone,
                   LocalDateTime dateOfBirth, String gender, int deptId, String password,
                   LocalDateTime admissionDate, int semester, String status, double cgpa, LocalDateTime createdAt) {
        this.studentId = studentId;
        this.rollNumber = rollNumber;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.deptId = deptId;
        this.password = password;
        this.admissionDate = admissionDate;
        this.semester = semester;
        this.status = status;
        this.cgpa = cgpa;
        this.createdAt = createdAt;
    }

    // ==================== GETTERS & SETTERS ====================

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDateTime admissionDate) {
        this.admissionDate = admissionDate;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * toString() - for debugging/logging
     */
    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", rollNumber='" + rollNumber + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", deptId=" + deptId +
                ", status='" + status + '\'' +
                ", cgpa=" + cgpa +
                '}';
    }
}
