package com.university.dao;

import com.university.models.Attendance;
import com.university.config.DatabaseConfig;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * AttendanceDAO - Data Access Object for Attendance Entity
 * Handles attendance records for students
 */
public class AttendanceDAO {

    public boolean addAttendance(Attendance attendance) {
        String sql = "INSERT INTO attendance (student_id, course_id, date, status, recorded_by) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, attendance.getStudentId());
            pstmt.setInt(2, attendance.getCourseId());
            pstmt.setDate(3, Date.valueOf(attendance.getDate()));
            pstmt.setString(4, attendance.getStatus());
            pstmt.setInt(5, attendance.getRecordedBy());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Add Attendance Error: " + e.getMessage());
        }
        
        return false;
    }

    public Attendance getAttendanceById(int attendanceId) {
        String sql = "SELECT * FROM attendance WHERE attendance_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, attendanceId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractAttendanceFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Get Attendance Error: " + e.getMessage());
        }
        
        return null;
    }

    public List<Attendance> getAttendanceByStudent(int studentId) {
        List<Attendance> attendances = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE student_id = ? ORDER BY date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                attendances.add(extractAttendanceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Get Attendance by Student Error: " + e.getMessage());
        }
        
        return attendances;
    }

    public List<Attendance> getAttendanceByCourse(int courseId) {
        List<Attendance> attendances = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE course_id = ? ORDER BY date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                attendances.add(extractAttendanceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Get Attendance by Course Error: " + e.getMessage());
        }
        
        return attendances;
    }

    public int getStudentAttendancePercentage(int studentId, int courseId) {
        String sql = "SELECT COUNT(*) as total, " +
                     "SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) as present " +
                     "FROM attendance WHERE student_id = ? AND course_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int total = rs.getInt("total");
                int present = rs.getInt("present");
                
                if (total == 0) return 0;
                return (int) ((present * 100) / total);
            }
            
        } catch (SQLException e) {
            System.err.println("Get Attendance Percentage Error: " + e.getMessage());
        }
        
        return 0;
    }

    public boolean updateAttendance(Attendance attendance) {
        String sql = "UPDATE attendance SET status = ? WHERE attendance_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, attendance.getStatus());
            pstmt.setInt(2, attendance.getAttendanceId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Update Attendance Error: " + e.getMessage());
        }
        
        return false;
    }

    public boolean deleteAttendance(int attendanceId) {
        String sql = "DELETE FROM attendance WHERE attendance_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, attendanceId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Delete Attendance Error: " + e.getMessage());
        }
        
        return false;
    }

    private Attendance extractAttendanceFromResultSet(ResultSet rs) throws SQLException {
        return new Attendance(
            rs.getInt("attendance_id"),
            rs.getInt("student_id"),
            rs.getInt("course_id"),
            rs.getDate("date").toLocalDate(),
            rs.getString("status"),
            rs.getInt("recorded_by"),
            rs.getDate("created_at") != null ? rs.getDate("created_at").toLocalDate() : null
        );
    }
}
