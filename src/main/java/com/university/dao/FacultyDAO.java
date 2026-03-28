package com.university.dao;

import com.university.models.Faculty;
import com.university.config.DatabaseConfig;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * FacultyDAO - Data Access Object for Faculty Entity
 * Handles faculty teachers and course assignments
 * CRUD operations (Create, Read, Update, Delete)
 * Authentication logic is handled by Service Layer
 */
public class FacultyDAO {

    // ==================== CREATE OPERATION ====================

    /**
     * ADD NEW FACULTY
     * 
     * @param faculty - Faculty object
     * @return true if successful, false otherwise
     */
    public boolean addFaculty(Faculty faculty) {
        String sql = "INSERT INTO faculty (emp_id, name, email, phone, dept_id, password, " +
                     "qualification, specialization, date_of_joining, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, faculty.getEmpId());
            pstmt.setString(2, faculty.getName());
            pstmt.setString(3, faculty.getEmail());
            pstmt.setString(4, faculty.getPhone());
            pstmt.setInt(5, faculty.getDeptId());
            pstmt.setString(6, faculty.getPassword());
            pstmt.setString(7, faculty.getQualification());
            pstmt.setString(8, faculty.getSpecialization());
            pstmt.setDate(9, Date.valueOf(faculty.getDateOfJoining()));
            pstmt.setString(10, faculty.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Add Faculty Error: " + e.getMessage());
        }
        
        return false;
    }

    // ==================== READ OPERATIONS ====================

    public Faculty getFacultyById(int facultyId) {
        String sql = "SELECT * FROM faculty WHERE faculty_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, facultyId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractFacultyFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Get Faculty Error: " + e.getMessage());
        }
        
        return null;
    }

    public Faculty getFacultyByEmpId(String empId) {
        String sql = "SELECT * FROM faculty WHERE emp_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, empId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractFacultyFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Get Faculty Error: " + e.getMessage());
        }
        
        return null;
    }

    public List<Faculty> getAllFaculty() {
        List<Faculty> faculty = new ArrayList<>();
        String sql = "SELECT * FROM faculty ORDER BY emp_id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                faculty.add(extractFacultyFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Get All Faculty Error: " + e.getMessage());
        }
        
        return faculty;
    }

    /**
     * GET FACULTY BY DEPARTMENT
     * 
     * @param deptId - Department ID
     * @return List of faculty in that department
     */
    public List<Faculty> getFacultyByDepartment(int deptId) {
        List<Faculty> faculty = new ArrayList<>();
        String sql = "SELECT * FROM faculty WHERE dept_id = ? ORDER BY emp_id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, deptId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                faculty.add(extractFacultyFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Get Faculty by Department Error: " + e.getMessage());
        }
        
        return faculty;
    }

    /**
     * GET ACTIVE FACULTY
     * 
     * @return List of active faculty
     */
    public List<Faculty> getActiveFaculty() {
        List<Faculty> faculty = new ArrayList<>();
        String sql = "SELECT * FROM faculty WHERE status = 'Active' ORDER BY emp_id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                faculty.add(extractFacultyFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Get Active Faculty Error: " + e.getMessage());
        }
        
        return faculty;
    }

    // ==================== UPDATE OPERATIONS ====================

    public boolean updateFaculty(Faculty faculty) {
        String sql = "UPDATE faculty SET name = ?, phone = ?, password = ?, " +
                     "qualification = ?, specialization = ?, status = ? WHERE faculty_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, faculty.getName());
            pstmt.setString(2, faculty.getPhone());
            pstmt.setString(3, faculty.getPassword());
            pstmt.setString(4, faculty.getQualification());
            pstmt.setString(5, faculty.getSpecialization());
            pstmt.setString(6, faculty.getStatus());
            pstmt.setInt(7, faculty.getFacultyId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Update Faculty Error: " + e.getMessage());
        }
        
        return false;
    }

    public boolean updatePassword(int facultyId, String newPassword) {
        String sql = "UPDATE faculty SET password = ? WHERE faculty_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, facultyId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Update Password Error: " + e.getMessage());
        }
        
        return false;
    }

    public boolean updateStatus(int facultyId, String status) {
        String sql = "UPDATE faculty SET status = ? WHERE faculty_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, facultyId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Update Status Error: " + e.getMessage());
        }
        
        return false;
    }

    // ==================== DELETE OPERATION ====================

    public boolean deleteFaculty(int facultyId) {
        String sql = "DELETE FROM faculty WHERE faculty_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, facultyId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Delete Faculty Error: " + e.getMessage());
        }
        
        return false;
    }

    // ==================== UTILITY METHODS ====================

    private Faculty extractFacultyFromResultSet(ResultSet rs) throws SQLException {
        return new Faculty(
            rs.getInt("faculty_id"),
            rs.getString("emp_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getInt("dept_id"),
            rs.getString("password"),
            rs.getString("qualification"),
            rs.getString("specialization"),
            rs.getDate("date_of_joining") != null ? rs.getDate("date_of_joining").toLocalDate() : null,
            rs.getString("status"),
            rs.getDate("created_at") != null ? rs.getDate("created_at").toLocalDate() : null
        );
    }

    public int getTotalFacultyCount() {
        String sql = "SELECT COUNT(*) as count FROM faculty";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
            
        } catch (SQLException e) {
            System.err.println("Get Total Count Error: " + e.getMessage());
        }
        
        return 0;
    }
}
