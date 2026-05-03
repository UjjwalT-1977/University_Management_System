package com.university.dao;

import com.university.models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class QuestionDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public QuestionDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Question> questionRowMapper = new RowMapper<Question>() {
        @Override
        public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
            Question q = new Question();
            q.setQuestionId(rs.getInt("question_id"));
            q.setQuizId(rs.getInt("quiz_id"));
            q.setQuestionText(rs.getString("question_text"));
            q.setOptionA(rs.getString("option_a"));
            q.setOptionB(rs.getString("option_b"));
            q.setOptionC(rs.getString("option_c"));
            q.setOptionD(rs.getString("option_d"));
            q.setCorrectOption(rs.getString("correct_option"));
            return q;
        }
    };

    public boolean addQuestion(Question question) {
        String sql = "INSERT INTO question (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            int rowsAffected = jdbcTemplate.update(sql, 
                    question.getQuizId(), 
                    question.getQuestionText(), 
                    question.getOptionA(), 
                    question.getOptionB(), 
                    question.getOptionC(), 
                    question.getOptionD(), 
                    question.getCorrectOption());
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error adding question: " + e.getMessage());
            return false;
        }
    }

    public List<Question> getQuestionsByQuizId(int quizId) {
        String sql = "SELECT * FROM question WHERE quiz_id = ?";
        return jdbcTemplate.query(sql, questionRowMapper, quizId);
    }
}