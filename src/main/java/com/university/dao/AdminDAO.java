package com.university.dao;

import com.university.models.Admin;
import com.university.config.DatabaseConfig;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminDAO - Data Access Object for Admin Entity
 * Handles retrieving admin credentials for login
 */
@Repository
public class AdminDAO {

    /**
     * Fetch an admin by their username for authentication purposes.
     */
    public Admin getAdminByUsername(String username) {
        String sql = "SELECT * FROM admin WHERE username = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractAdminFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Get Admin Error: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Update an admin's password (useful for the 'Change Password' feature)
     */
    public boolean updatePassword(int adminId, String newPassword) {
        String sql = "UPDATE admin SET password = ? WHERE admin_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, adminId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Update Admin Password Error: " + e.getMessage());
        }
        
        return false;
    }

    private Admin extractAdminFromResultSet(ResultSet rs) throws SQLException {
        return new Admin(
            rs.getInt("admin_id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("email"),
            rs.getString("name"),
            rs.getString("phone"),
            rs.getDate("created_at") != null ? rs.getDate("created_at").toLocalDate() : null,
            rs.getDate("updated_at") != null ? rs.getDate("updated_at").toLocalDate() : null
        );
    }
    /**
     * ADD NEW ADMIN
     */
    public boolean addAdmin(Admin admin) {
        String sql = "INSERT INTO admin (username, password, email, name, phone) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, admin.getUsername());
            pstmt.setString(2, admin.getPassword());
            pstmt.setString(3, admin.getEmail());
            pstmt.setString(4, admin.getName());
            pstmt.setString(5, admin.getPhone());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Add Admin Error: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * GET ADMIN BY ID
     */
    public Admin getAdminById(int adminId) {
        String sql = "SELECT * FROM admin WHERE admin_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, adminId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractAdminFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Get Admin by ID Error: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * GET ALL ADMINS
     */
    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT * FROM admin ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                admins.add(extractAdminFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Get All Admins Error: " + e.getMessage());
        }
        
        return admins;
    }

    /**
     * UPDATE ADMIN DETAILS (Email, Name, Phone)
     */
    public boolean updateAdmin(Admin admin) {
        String sql = "UPDATE admin SET email = ?, name = ?, phone = ?, updated_at = NOW() WHERE admin_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, admin.getEmail());
            pstmt.setString(2, admin.getName());
            pstmt.setString(3, admin.getPhone());
            pstmt.setInt(4, admin.getAdminId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Update Admin Error: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * DELETE ADMIN
     */
    public boolean deleteAdmin(int adminId) {
        String sql = "DELETE FROM admin WHERE admin_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, adminId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Delete Admin Error: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * COUNT TOTAL ADMINS (useful for business rule: prevent deleting last admin)
     */
    public int countAdmins() {
        String sql = "SELECT COUNT(*) FROM admin";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Count Admins Error: " + e.getMessage());
        }
        
        return 0;
    }
}