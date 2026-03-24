package com.university.dao;

import com.university.models.Marks;
import com.university.config.DatabaseConfig;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * MarksDAO - Data Access Object for Marks Entity
 * Handles student grades and marks management
 */
public class MarksDAO {

    public boolean addMarks(Marks marks) {
        String sql = "INSERT INTO marks (student_id, course_id, internal_marks, external_marks, " +
                     "total_marks, grade, recorded_by) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, marks.getStudentId());
            pstmt.setInt(2, marks.getCourseId());
            pstmt.setDouble(3, marks.getInternalMarks());
            pstmt.setDouble(4, marks.getExternalMarks());
            pstmt.setDouble(5, marks.getTotalMarks());
            pstmt.setString(6, marks.getGrade());
            pstmt.setInt(7, marks.getRecordedBy());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Add Marks Error: " + e.getMessage());
        }
        
        return false;
    }

    public Marks getMarksById(int marksId) {
        String sql = "SELECT * FROM marks WHERE marks_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, marksId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractMarksFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Get Marks Error: " + e.getMessage());
        }
        
        return null;
    }

    public Marks getMarksByStudentAndCourse(int studentId, int courseId) {
        String sql = "SELECT * FROM marks WHERE student_id = ? AND course_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractMarksFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Get Marks Error: " + e.getMessage());
        }
        
        return null;
    }

    public List<Marks> getMarksByStudent(int studentId) {
        List<Marks> marksList = new ArrayList<>();
        String sql = "SELECT * FROM marks WHERE student_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                marksList.add(extractMarksFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Get Marks by Student Error: " + e.getMessage());
        }
        
        return marksList;
    }

    public List<Marks> getMarksByCourse(int courseId) {
        List<Marks> marksList = new ArrayList<>();
        String sql = "SELECT * FROM marks WHERE course_id = ? ORDER BY total_marks DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                marksList.add(extractMarksFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Get Marks by Course Error: " + e.getMessage());
        }
        
        return marksList;
    }

    public double getStudentAverageMarks(int studentId) {
        String sql = "SELECT AVG(total_marks) as average FROM marks WHERE student_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double avg = rs.getDouble("average");
                return Double.isNaN(avg) ? 0 : avg;
            }
            
        } catch (SQLException e) {
            System.err.println("Get Average Marks Error: " + e.getMessage());
        }
        
        return 0;
    }

    public List<Marks> getMarksByGrade(String grade) {
        List<Marks> marksList = new ArrayList<>();
        String sql = "SELECT * FROM marks WHERE grade = ? ORDER BY student_id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, grade);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                marksList.add(extractMarksFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Get Marks by Grade Error: " + e.getMessage());
        }
        
        return marksList;
    }

    /**
     * GET GRADE DISTRIBUTION FOR COURSE
     * Shows how many students got each grade
     */
    public List<String[]> getGradeDistribution(int courseId) {
        List<String[]> distribution = new ArrayList<>();
        String sql = "SELECT grade, COUNT(*) as count FROM marks WHERE course_id = ? GROUP BY grade ORDER BY grade DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                distribution.add(new String[]{rs.getString("grade"), String.valueOf(rs.getInt("count"))});
            }
            
        } catch (SQLException e) {
            System.err.println("Get Grade Distribution Error: " + e.getMessage());
        }
        
        return distribution;
    }

    public boolean updateMarks(Marks marks) {
        String sql = "UPDATE marks SET internal_marks = ?, external_marks = ?, total_marks = ?, " +
                     "grade = ? WHERE marks_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, marks.getInternalMarks());
            pstmt.setDouble(2, marks.getExternalMarks());
            pstmt.setDouble(3, marks.getTotalMarks());
            pstmt.setString(4, marks.getGrade());
            pstmt.setInt(5, marks.getMarksId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Update Marks Error: " + e.getMessage());
        }
        
        return false;
    }

    public boolean deleteMarks(int marksId) {
        String sql = "DELETE FROM marks WHERE marks_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, marksId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Delete Marks Error: " + e.getMessage());
        }
        
        return false;
    }

    private Marks extractMarksFromResultSet(ResultSet rs) throws SQLException {
        return new Marks(
            rs.getInt("marks_id"),
            rs.getInt("student_id"),
            rs.getInt("course_id"),
            rs.getDouble("internal_marks"),
            rs.getDouble("external_marks"),
            rs.getDouble("total_marks"),
            rs.getString("grade"),
            rs.getInt("recorded_by"),
            rs.getDate("created_at") != null ? rs.getDate("created_at").toLocalDate() : null,
            rs.getDate("updated_at") != null ? rs.getDate("updated_at").toLocalDate() : null
        );
    }
}
