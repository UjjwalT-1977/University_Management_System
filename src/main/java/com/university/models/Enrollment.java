package com.university.models;

import java.time.LocalDate;

/**
 * Enrollment Entity Class
 * Represents a student's enrollment in a course
 * Maps to 'enrollment' table in database
 */
public class Enrollment {
    private int enrollmentId;
    private int studentId;           // Foreign Key to Student
    private int courseId;            // Foreign Key to Course
    private LocalDate enrollmentDate;
    private double cgpaAtEnrollment;
    private String status;           // Enrolled/Dropped/Completed

    // ==================== CONSTRUCTORS ====================

    public Enrollment() {}

    public Enrollment(int studentId, int courseId, LocalDate enrollmentDate) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
    }

    public Enrollment(int enrollmentId, int studentId, int courseId, 
                      LocalDate enrollmentDate, double cgpaAtEnrollment, String status) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
        this.cgpaAtEnrollment = cgpaAtEnrollment;
        this.status = status;
    }

    // ==================== GETTERS & SETTERS ====================

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public double getCgpaAtEnrollment() {
        return cgpaAtEnrollment;
    }

    public void setCgpaAtEnrollment(double cgpaAtEnrollment) {
        this.cgpaAtEnrollment = cgpaAtEnrollment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "enrollmentId=" + enrollmentId +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                ", status='" + status + '\'' +
                '}';
    }
}
