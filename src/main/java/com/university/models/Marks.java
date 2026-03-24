package com.university.models;

import java.time.LocalDate;

/**
 * Marks Entity Class
 * Represents marks/grades for students in courses
 * Maps to 'marks' table in database
 */
public class Marks {
    private int marksId;
    private int studentId;           // Foreign Key to Student
    private int courseId;            // Foreign Key to Course
    private double internalMarks;    // Internal exam marks
    private double externalMarks;    // External exam marks
    private double totalMarks;       // Total = internal + external
    private String grade;            // A, B, C, D, F
    private int recordedBy;          // Faculty ID who recorded (Foreign Key to Faculty)
    private LocalDate createdAt;
    private LocalDate updatedAt;

    // ==================== CONSTRUCTORS ====================

    public Marks() {}

    public Marks(int studentId, int courseId, double internalMarks, double externalMarks) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.internalMarks = internalMarks;
        this.externalMarks = externalMarks;
        this.totalMarks = internalMarks + externalMarks;
    }

    public Marks(int marksId, int studentId, int courseId, double internalMarks, 
                 double externalMarks, double totalMarks, String grade, int recordedBy,
                 LocalDate createdAt, LocalDate updatedAt) {
        this.marksId = marksId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.internalMarks = internalMarks;
        this.externalMarks = externalMarks;
        this.totalMarks = totalMarks;
        this.grade = grade;
        this.recordedBy = recordedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ==================== GETTERS & SETTERS ====================

    public int getMarksId() {
        return marksId;
    }

    public void setMarksId(int marksId) {
        this.marksId = marksId;
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

    public double getInternalMarks() {
        return internalMarks;
    }

    public void setInternalMarks(double internalMarks) {
        this.internalMarks = internalMarks;
    }

    public double getExternalMarks() {
        return externalMarks;
    }

    public void setExternalMarks(double externalMarks) {
        this.externalMarks = externalMarks;
    }

    public double getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(double totalMarks) {
        this.totalMarks = totalMarks;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
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

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Calculate grade based on total marks
     * A: 90-100, B: 80-89, C: 70-79, D: 60-69, F: <60
     */
    public void calculateGrade() {
        if (totalMarks >= 90) {
            this.grade = "A";
        } else if (totalMarks >= 80) {
            this.grade = "B";
        } else if (totalMarks >= 70) {
            this.grade = "C";
        } else if (totalMarks >= 60) {
            this.grade = "D";
        } else {
            this.grade = "F";
        }
    }

    @Override
    public String toString() {
        return "Marks{" +
                "marksId=" + marksId +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                ", totalMarks=" + totalMarks +
                ", grade='" + grade + '\'' +
                '}';
    }
}
