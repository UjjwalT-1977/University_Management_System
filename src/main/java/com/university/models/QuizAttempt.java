package com.university.models;
import java.time.LocalDateTime;

public class QuizAttempt {
    private int attemptId;
    private int studentId;
    private int quizId;
    private double score;
    private int totalQuestions;
    private LocalDateTime attemptedAt;

    public QuizAttempt() {}

    // Getters and Setters
    public int getAttemptId() { return attemptId; }
    public void setAttemptId(int attemptId) { this.attemptId = attemptId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public LocalDateTime getAttemptedAt() { return attemptedAt; }
    public void setAttemptedAt(LocalDateTime attemptedAt) { this.attemptedAt = attemptedAt; }
}