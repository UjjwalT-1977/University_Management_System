package com.university.models;

import java.time.LocalDate;

/**
 * Course Entity Class
 * Represents a course offered by the university
 * Maps to 'course' table in database
 */
public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private int deptId;              // Foreign Key to Department
    private int facultyId;           // Foreign Key to Faculty (teacher)
    private int credits;
    private int maxCapacity;
    private int currentEnrollment;
    private int semester;
    private int year;
    private LocalDate createdAt;

    // ==================== CONSTRUCTORS ====================

    public Course() {}

    public Course(String courseCode, String courseName, int deptId, int facultyId, int credits) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.deptId = deptId;
        this.facultyId = facultyId;
        this.credits = credits;
    }

    public Course(int courseId, String courseCode, String courseName, int deptId, 
                  int facultyId, int credits, int maxCapacity, int currentEnrollment,
                  int semester, int year, LocalDate createdAt) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.deptId = deptId;
        this.facultyId = facultyId;
        this.credits = credits;
        this.maxCapacity = maxCapacity;
        this.currentEnrollment = currentEnrollment;
        this.semester = semester;
        this.year = year;
        this.createdAt = createdAt;
    }

    // ==================== GETTERS & SETTERS ====================

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getCurrentEnrollment() {
        return currentEnrollment;
    }

    public void setCurrentEnrollment(int currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", deptId=" + deptId +
                ", credits=" + credits +
                '}';
    }
}
