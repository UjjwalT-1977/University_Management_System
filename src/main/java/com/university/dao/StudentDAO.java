package com.university.dao;

import com.university.models.Student;
import com.university.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentDAO - Data Access Object for Student entity
 * Handles all database operations for student records
 * NOTE: Authentication is handled by Service Layer, not here
 */
public class StudentDAO {
    
    /**
     * Add a new student to the database
     * @param student Student object to be added
     * @return true if successful, false otherwise
     */
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO student (roll_number, name, email, phone, date_of_birth, gender, " +
                     "dept_id, password, admission_date, semester, status, cgpa, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, student.getRollNumber());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getPhone());
            pstmt.setDate(5, java.sql.Date.valueOf(student.getDateOfBirth()));
            pstmt.setString(6, student.getGender());
            pstmt.setInt(7, student.getDeptId());
            pstmt.setString(8, student.getPassword());
            pstmt.setDate(9, java.sql.Date.valueOf(student.getAdmissionDate()));
            pstmt.setInt(10, student.getSemester());
            pstmt.setString(11, student.getStatus());
            pstmt.setDouble(12, student.getCgpa());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get student by ID
     * @param studentId Student ID
     * @return Student object if found, null otherwise
     */
    public Student getStudentById(int studentId) {
        String sql = "SELECT * FROM student WHERE student_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving student: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get student by roll number
     * @param rollNumber Student roll number
     * @return Student object if found, null otherwise
     */
    public Student getStudentByRollNumber(String rollNumber) {
        String sql = "SELECT * FROM student WHERE roll_number = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, rollNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving student by roll number: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get all students
     * @return List of all Student objects
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student ORDER BY student_id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all students: " + e.getMessage());
        }
        return students;
    }
    
    /**
     * Get students by department
     * @param deptId Department ID
     * @return List of Student objects in that department
     */
    public List<Student> getStudentsByDepartment(int deptId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student WHERE dept_id = ? ORDER BY roll_number";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, deptId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving students by department: " + e.getMessage());
        }
        return students;
    }
    
    /**
     * Get students by status
     * @param status Student status (Active/Inactive/Suspended)
     * @return List of Student objects with that status
     */
    public List<Student> getStudentsByStatus(String status) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student WHERE status = ? ORDER BY student_id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving students by status: " + e.getMessage());
        }
        return students;
    }
    
    /**
     * Update student information
     * @param student Updated Student object
     * @return true if successful, false otherwise
     */
    public boolean updateStudent(Student student) {
        String sql = "UPDATE student SET roll_number = ?, name = ?, email = ?, phone = ?, " +
                     "date_of_birth = ?, gender = ?, dept_id = ?, semester = ?, status = ?, cgpa = ? " +
                     "WHERE student_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, student.getRollNumber());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getPhone());
            pstmt.setDate(5, java.sql.Date.valueOf(student.getDateOfBirth()));
            pstmt.setString(6, student.getGender());
            pstmt.setInt(7, student.getDeptId());
            pstmt.setInt(8, student.getSemester());
            pstmt.setString(9, student.getStatus());
            pstmt.setDouble(10, student.getCgpa());
            pstmt.setInt(11, student.getStudentId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update student password
     * @param studentId Student ID
     * @param newPassword New password
     * @return true if successful, false otherwise
     */
    public boolean updatePassword(int studentId, String newPassword) {
        String sql = "UPDATE student SET password = ? WHERE student_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, studentId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update student CGPA
     * @param studentId Student ID
     * @param cgpa New CGPA value
     * @return true if successful, false otherwise
     */
    public boolean updateCGPA(int studentId, double cgpa) {
        String sql = "UPDATE student SET cgpa = ? WHERE student_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, cgpa);
            pstmt.setInt(2, studentId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating CGPA: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete student
     * @param studentId Student ID
     * @return true if successful, false otherwise
     */
    public boolean deleteStudent(int studentId) {
        String sql = "DELETE FROM student WHERE student_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get total student count
     * @return Total number of students
     */
    public int getTotalStudentCount() {
        String sql = "SELECT COUNT(*) FROM student";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting student count: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Search students by name, roll number, or email
     * @param searchTerm Search term
     * @return List of matching Student objects
     */
    public List<Student> searchStudents(String searchTerm) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student WHERE name LIKE ? OR roll_number LIKE ? OR email LIKE ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String term = "%" + searchTerm + "%";
            pstmt.setString(1, term);
            pstmt.setString(2, term);
            pstmt.setString(3, term);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching students: " + e.getMessage());
        }
        return students;
    }
    
    /**
     * Extract Student object from ResultSet
     * @param rs ResultSet from database query
     * @return Student object
     */
    private Student extractFromResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setRollNumber(rs.getString("roll_number"));
        student.setName(rs.getString("name"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        student.setGender(rs.getString("gender"));
        student.setDeptId(rs.getInt("dept_id"));
        student.setPassword(rs.getString("password"));
        student.setAdmissionDate(rs.getDate("admission_date").toLocalDate());
        student.setSemester(rs.getInt("semester"));
        student.setStatus(rs.getString("status"));
        student.setCgpa(rs.getDouble("cgpa"));
        student.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return student;
    }
}
