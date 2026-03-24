package com.university.dao;

import com.university.models.Course;
import com.university.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CourseDAO - Data Access Object for Course Entity
 */
public class CourseDAO {

    public boolean addCourse(Course course) {
        String sql = "INSERT INTO course (course_code, course_name, dept_id, faculty_id, " +
                     "credits, max_capacity, semester, year) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setInt(3, course.getDeptId());
            pstmt.setInt(4, course.getFacultyId());
            pstmt.setInt(5, course.getCredits());
            pstmt.setInt(6, course.getMaxCapacity());
            pstmt.setInt(7, course.getSemester());
            pstmt.setInt(8, course.getYear());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Add Course Error: " + e.getMessage());
        }
        
        return false;
    }

    public Course getCourseById(int courseId) {
        String sql = "SELECT * FROM course WHERE course_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractCourseFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Get Course Error: " + e.getMessage());
        }
        
        return null;
    }

    public Course getCourseByCourseCode(String courseCode) {
        String sql = "SELECT * FROM course WHERE course_code = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, courseCode);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractCourseFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Get Course Error: " + e.getMessage());
        }
        
        return null;
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM course ORDER BY course_code";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                courses.add(extractCourseFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Get All Courses Error: " + e.getMessage());
        }
        
        return courses;
    }

    public List<Course> getCoursesByDepartment(int deptId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM course WHERE dept_id = ? ORDER BY course_code";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, deptId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                courses.add(extractCourseFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Get Courses by Department Error: " + e.getMessage());
        }
        
        return courses;
    }

    public List<Course> getCoursesByFaculty(int facultyId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM course WHERE faculty_id = ? ORDER BY course_code";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, facultyId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                courses.add(extractCourseFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Get Courses by Faculty Error: " + e.getMessage());
        }
        
        return courses;
    }

    public boolean updateCourse(Course course) {
        String sql = "UPDATE course SET course_name = ?, faculty_id = ?, credits = ?, " +
                     "max_capacity = ?, current_enrollment = ?, semester = ?, year = ? " +
                     "WHERE course_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, course.getCourseName());
            pstmt.setInt(2, course.getFacultyId());
            pstmt.setInt(3, course.getCredits());
            pstmt.setInt(4, course.getMaxCapacity());
            pstmt.setInt(5, course.getCurrentEnrollment());
            pstmt.setInt(6, course.getSemester());
            pstmt.setInt(7, course.getYear());
            pstmt.setInt(8, course.getCourseId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Update Course Error: " + e.getMessage());
        }
        
        return false;
    }

    public boolean updateEnrollmentCount(int courseId, int count) {
        String sql = "UPDATE course SET current_enrollment = ? WHERE course_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, count);
            pstmt.setInt(2, courseId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Update Enrollment Count Error: " + e.getMessage());
        }
        
        return false;
    }

    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM course WHERE course_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Delete Course Error: " + e.getMessage());
        }
        
        return false;
    }

    private Course extractCourseFromResultSet(ResultSet rs) throws SQLException {
        return new Course(
            rs.getInt("course_id"),
            rs.getString("course_code"),
            rs.getString("course_name"),
            rs.getInt("dept_id"),
            rs.getInt("faculty_id"),
            rs.getInt("credits"),
            rs.getInt("max_capacity"),
            rs.getInt("current_enrollment"),
            rs.getInt("semester"),
            rs.getInt("year"),
            rs.getDate("created_at") != null ? rs.getDate("created_at").toLocalDate() : null
        );
    }
}
