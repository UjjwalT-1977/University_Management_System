package com.university.dao;

import com.university.models.Enrollment;
import com.university.config.DatabaseConfig;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * EnrollmentDAO - Data Access Object for Enrollment Entity
 * Handles student enrollments in courses
 */
public class EnrollmentDAO {

    public boolean addEnrollment(Enrollment enrollment) {
        String sql = "INSERT INTO enrollment (student_id, course_id, enrollment_date, " +
                     "cgpa_at_enrollment, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getCourseId());
            pstmt.setDate(3, Date.valueOf(enrollment.getEnrollmentDate() != null ? 
                                          enrollment.getEnrollmentDate() : LocalDate.now()));
            pstmt.setDouble(4, enrollment.getCgpaAtEnrollment());
            pstmt.setString(5, enrollment.getStatus() != null ? enrollment.getStatus() : "Enrolled");
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Add Enrollment Error: " + e.getMessage());
        }
        
        return false;
    }

    public Enrollment getEnrollmentById(int enrollmentId) {
        String sql = "SELECT * FROM enrollment WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, enrollmentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractEnrollmentFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Get Enrollment Error: " + e.getMessage());
        }
        
        return null;
    }

    public List<Enrollment> getEnrollmentsByStudent(int studentId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollment WHERE student_id = ? ORDER BY enrollment_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                enrollments.add(extractEnrollmentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Get Enrollments by Student Error: " + e.getMessage());
        }
        
        return enrollments;
    }

    public List<Enrollment> getEnrollmentsByCourse(int courseId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollment WHERE course_id = ? ORDER BY enrollment_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                enrollments.add(extractEnrollmentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Get Enrollments by Course Error: " + e.getMessage());
        }
        
        return enrollments;
    }

    public int getEnrollmentCountForCourse(int courseId) {
        String sql = "SELECT COUNT(*) as count FROM enrollment WHERE course_id = ? AND status = 'Enrolled'";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
            
        } catch (SQLException e) {
            System.err.println("Get Enrollment Count Error: " + e.getMessage());
        }
        
        return 0;
    }

    public boolean updateEnrollmentStatus(int enrollmentId, String status) {
        String sql = "UPDATE enrollment SET status = ? WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, enrollmentId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Update Enrollment Status Error: " + e.getMessage());
        }
        
        return false;
    }

    public boolean deleteEnrollment(int enrollmentId) {
        String sql = "DELETE FROM enrollment WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, enrollmentId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Delete Enrollment Error: " + e.getMessage());
        }
        
        return false;
    }

    private Enrollment extractEnrollmentFromResultSet(ResultSet rs) throws SQLException {
        return new Enrollment(
            rs.getInt("enrollment_id"),
            rs.getInt("student_id"),
            rs.getInt("course_id"),
            rs.getDate("enrollment_date").toLocalDate(),
            rs.getDouble("cgpa_at_enrollment"),
            rs.getString("status")
        );
    }
}
