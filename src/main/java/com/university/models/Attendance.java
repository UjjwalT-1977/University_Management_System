package com.university.models;

import java.time.LocalDate;

/**
 * Attendance Entity Class
 * Represents attendance records for students in courses
 * Maps to 'attendance' table in database
 */
public class Attendance {
    private int attendanceId;
    private int studentId;           // Foreign Key to Student
    private int courseId;            // Foreign Key to Course
    private LocalDate date;
    private String status;           // Present/Absent/Leave
    private int recordedBy;          // Faculty ID who recorded (Foreign Key to Faculty)
    private LocalDate createdAt;

    // ==================== CONSTRUCTORS ====================

    public Attendance() {}

    public Attendance(int studentId, int courseId, LocalDate date, String status) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.date = date;
        this.status = status;
    }

    public Attendance(int attendanceId, int studentId, int courseId, LocalDate date, 
                      String status, int recordedBy, LocalDate createdAt) {
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.date = date;
        this.status = status;
        this.recordedBy = recordedBy;
        this.createdAt = createdAt;
    }

    // ==================== GETTERS & SETTERS ====================

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(int recordedBy) {
        this.recordedBy = recordedBy;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceId=" + attendanceId +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                ", date=" + date +
                ", status='" + status + '\'' +
                '}';
    }
}
