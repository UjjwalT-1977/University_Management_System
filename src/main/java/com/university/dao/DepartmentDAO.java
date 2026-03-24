package com.university.dao;

import com.university.models.Department;
import com.university.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DepartmentDAO - Data Access Object for Department Entity
 */
public class DepartmentDAO {

    public boolean addDepartment(Department department) {
        String sql = "INSERT INTO department (dept_name, dept_code, hod_name, phone, email) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, department.getDeptName());
            pstmt.setString(2, department.getDeptCode());
            pstmt.setString(3, department.getHodName());
            pstmt.setString(4, department.getPhone());
            pstmt.setString(5, department.getEmail());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Add Department Error: " + e.getMessage());
        }
        
        return false;
    }

    public Department getDepartmentById(int deptId) {
        String sql = "SELECT * FROM department WHERE dept_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, deptId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractDepartmentFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Get Department Error: " + e.getMessage());
        }
        
        return null;
    }

    public Department getDepartmentByCode(String deptCode) {
        String sql = "SELECT * FROM department WHERE dept_code = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, deptCode);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractDepartmentFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Get Department Error: " + e.getMessage());
        }
        
        return null;
    }

    public List<Department> getAllDepartments() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM department ORDER BY dept_code";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                departments.add(extractDepartmentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Get All Departments Error: " + e.getMessage());
        }
        
        return departments;
    }

    public boolean updateDepartment(Department department) {
        String sql = "UPDATE department SET dept_name = ?, hod_name = ?, phone = ?, email = ? " +
                     "WHERE dept_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, department.getDeptName());
            pstmt.setString(2, department.getHodName());
            pstmt.setString(3, department.getPhone());
            pstmt.setString(4, department.getEmail());
            pstmt.setInt(5, department.getDeptId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Update Department Error: " + e.getMessage());
        }
        
        return false;
    }

    public boolean deleteDepartment(int deptId) {
        String sql = "DELETE FROM department WHERE dept_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, deptId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Delete Department Error: " + e.getMessage());
        }
        
        return false;
    }

    private Department extractDepartmentFromResultSet(ResultSet rs) throws SQLException {
        return new Department(
            rs.getInt("dept_id"),
            rs.getString("dept_name"),
            rs.getString("dept_code"),
            rs.getString("hod_name"),
            rs.getString("phone"),
            rs.getString("email"),
            rs.getDate("created_at") != null ? rs.getDate("created_at").toLocalDate() : null
        );
    }
}
