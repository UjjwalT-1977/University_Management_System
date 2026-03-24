package com.university.models;

import java.time.LocalDate;

/**
 * Faculty Entity Class
 * Represents a teacher/faculty member in the university
 * Maps to 'faculty' table in database
 */
public class Faculty {
    private int facultyId;
    private String empId;
    private String name;
    private String email;
    private String phone;
    private int deptId;              // Foreign Key to Department
    private String password;
    private String qualification;
    private String specialization;
    private LocalDate dateOfJoining;
    private String status;           // Active/Inactive/Leave
    private LocalDate createdAt;

    // ==================== CONSTRUCTORS ====================

    public Faculty() {}

    public Faculty(String empId, String name, String email, int deptId, String password) {
        this.empId = empId;
        this.name = name;
        this.email = email;
        this.deptId = deptId;
        this.password = password;
    }

    public Faculty(int facultyId, String empId, String name, String email, String phone, 
                   int deptId, String password, String qualification, String specialization,
                   LocalDate dateOfJoining, String status, LocalDate createdAt) {
        this.facultyId = facultyId;
        this.empId = empId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.deptId = deptId;
        this.password = password;
        this.qualification = qualification;
        this.specialization = specialization;
        this.dateOfJoining = dateOfJoining;
        this.status = status;
        this.createdAt = createdAt;
    }

    // ==================== GETTERS & SETTERS ====================

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
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

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public LocalDate getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(LocalDate dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "facultyId=" + facultyId +
                ", empId='" + empId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", deptId=" + deptId +
                ", status='" + status + '\'' +
                '}';
    }
}
