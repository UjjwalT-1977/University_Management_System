package com.university.dao;

import com.university.models.Admin;
import com.university.config.DatabaseConfig;
import java.sql.*;

/**
 * AdminDAO - Data Access Object for Admin Entity
 * Handles retrieving admin credentials for login
 */
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
}