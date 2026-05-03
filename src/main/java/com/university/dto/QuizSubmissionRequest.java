package com.university.dto;

import java.util.Map;

public class QuizSubmissionRequest {
    private int studentId;
    private int quizId;
    // Maps Question ID to the selected Option (e.g., { 1: "A", 2: "C" })
    private Map<Integer, String> answers; 

    // Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }
    public Map<Integer, String> getAnswers() { return answers; }
    public void setAnswers(Map<Integer, String> answers) { this.answers = answers; }
}