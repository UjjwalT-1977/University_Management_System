package com.university.dao;

import com.university.models.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class QuizDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public QuizDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Maps a row from the database into our Java Quiz object
    private final RowMapper<Quiz> quizRowMapper = new RowMapper<Quiz>() {
        @Override
        public Quiz mapRow(ResultSet rs, int rowNum) throws SQLException {
            Quiz quiz = new Quiz();
            quiz.setQuizId(rs.getInt("quiz_id"));
            quiz.setCourseId(rs.getInt("course_id"));
            quiz.setFacultyId(rs.getInt("faculty_id"));
            quiz.setTitle(rs.getString("title"));
            quiz.setDurationMinutes(rs.getInt("duration_minutes"));
            if (rs.getTimestamp("created_at") != null) {
                quiz.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            return quiz;
        }
    };

    public boolean createQuiz(Quiz quiz) {
        String sql = "INSERT INTO quiz (course_id, faculty_id, title, duration_minutes) VALUES (?, ?, ?, ?)";
        try {
            int rowsAffected = jdbcTemplate.update(sql, 
                    quiz.getCourseId(), 
                    quiz.getFacultyId(), 
                    quiz.getTitle(), 
                    quiz.getDurationMinutes());
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error creating quiz: " + e.getMessage());
            return false;
        }
    }

    public List<Quiz> getQuizzesByCourseId(int courseId) {
        // Order by created_at DESC so the newest quizzes show up first
        String sql = "SELECT * FROM quiz WHERE course_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, quizRowMapper, courseId);
    }
}