package com.university.models;

import java.time.LocalDate;

/**
 * Department Entity Class
 * Represents a department in the university
 * Maps to 'department' table in database
 */
public class Department {
    private int deptId;
    private String deptName;
    private String deptCode;
    private String hodName;
    private String phone;
    private String email;
    private LocalDate createdAt;

    // ==================== CONSTRUCTORS ====================

    public Department() {}

    public Department(String deptName, String deptCode, String hodName, String email, String phone) {
        this.deptName = deptName;
        this.deptCode = deptCode;
        this.hodName = hodName;
        this.email = email;
        this.phone = phone;
    }

    public Department(int deptId, String deptName, String deptCode, String hodName, 
                      String phone, String email, LocalDate createdAt) {
        this.deptId = deptId;
        this.deptName = deptName;
        this.deptCode = deptCode;
        this.hodName = hodName;
        this.phone = phone;
        this.email = email;
        this.createdAt = createdAt;
    }

    // ==================== GETTERS & SETTERS ====================

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getHodName() {
        return hodName;
    }

    public void setHodName(String hodName) {
        this.hodName = hodName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Department{" +
                "deptId=" + deptId +
                ", deptName='" + deptName + '\'' +
                ", deptCode='" + deptCode + '\'' +
                ", hodName='" + hodName + '\'' +
                '}';
    }
}
