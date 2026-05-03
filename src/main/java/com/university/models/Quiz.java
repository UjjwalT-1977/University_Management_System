package com.university.models;

import java.time.LocalDateTime;

public class Quiz {
    private int quizId;
    private int courseId;
    private int facultyId;
    private String title;
    private int durationMinutes;
    private LocalDateTime createdAt;

    public Quiz() {}

    public Quiz(int courseId, int facultyId, String title, int durationMinutes) {
        this.courseId = courseId;
        this.facultyId = facultyId;
        this.title = title;
        this.durationMinutes = durationMinutes;
    }

    // Getters and Setters
    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public int getFacultyId() { return facultyId; }
    public void setFacultyId(int facultyId) { this.facultyId = facultyId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}