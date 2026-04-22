package com.university.dto;

/**
 * EnrollmentRequest DTO
 * Data Transfer Object for Enrollment API requests
 * Used by Admin to enroll students into courses
 */
public class EnrollmentRequest {
    private int studentId;
    private int courseId;
    private int adminId;  // The admin performing the enrollment

    // ==================== CONSTRUCTORS ====================

    public EnrollmentRequest() {}

    public EnrollmentRequest(int studentId, int courseId, int adminId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.adminId = adminId;
    }

    // ==================== GETTERS & SETTERS ====================

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

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    @Override
    public String toString() {
        return "EnrollmentRequest{" +
                "studentId=" + studentId +
                ", courseId=" + courseId +
                ", adminId=" + adminId +
                '}';
    }
}
