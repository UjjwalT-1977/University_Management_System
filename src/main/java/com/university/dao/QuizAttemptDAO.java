package com.university.dao;

import com.university.models.QuizAttempt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class QuizAttemptDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public QuizAttemptDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean saveAttempt(QuizAttempt attempt) {
        String sql = "INSERT INTO quiz_attempt (student_id, quiz_id, score, total_questions) VALUES (?, ?, ?, ?)";
        try {
            int rowsAffected = jdbcTemplate.update(sql, 
                    attempt.getStudentId(), 
                    attempt.getQuizId(), 
                    attempt.getScore(), 
                    attempt.getTotalQuestions());
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error saving quiz attempt: " + e.getMessage());
            return false; // Will fail if they already took it (because of our UNIQUE constraint in SQL)
        }
    }

    // Helper method to check if a student has already taken this test
    public boolean hasStudentAttemptedQuiz(int studentId, int quizId) {
        String sql = "SELECT COUNT(*) FROM quiz_attempt WHERE student_id = ? AND quiz_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentId, quizId);
        return count != null && count > 0;
    }
}