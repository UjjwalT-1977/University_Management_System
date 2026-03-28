package com.university.services;

import com.university.dao.AdminDAO;
import com.university.dao.StudentDAO;
import com.university.dao.FacultyDAO;
import com.university.models.Admin;
import com.university.models.Student;
import com.university.models.Faculty;

import java.util.logging.Logger;

/**
 * Business Logic Layer for Authentication and Security
 */
public class AuthService {

    private final AdminDAO adminDAO;
    private final StudentDAO studentDAO;
    private final FacultyDAO facultyDAO;
    
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());

    public AuthService() {
        this.adminDAO = new AdminDAO();
        this.studentDAO = new StudentDAO();
        this.facultyDAO = new FacultyDAO();
    }

    /**
     * Universal Login Method
     * @param identifier The username, student roll number, or faculty employee ID
     * @param password The provided password
     * @param role "ADMIN", "STUDENT", or "FACULTY"
     * @return The authenticated user object (Admin/Student/Faculty), or null if login fails.
     */
    public Object authenticate(String identifier, String password, String role) {
        logger.info("Authentication attempt for role: " + role + " with identifier: " + identifier);

        if (identifier == null || password == null || identifier.trim().isEmpty() || password.trim().isEmpty()) {
            logger.warning("Authentication failed: Empty credentials provided.");
            return null;
        }

        switch (role.toUpperCase()) {
            case "ADMIN":
                return authenticateAdmin(identifier, password);
            case "STUDENT":
                return authenticateStudent(identifier, password);
            case "FACULTY":
                return authenticateFaculty(identifier, password);
            default:
                logger.warning("Authentication failed: Unknown role specified.");
                return null;
        }
    }

    private Admin authenticateAdmin(String username, String password) {
        Admin admin = adminDAO.getAdminByUsername(username);
        
        // Check if admin exists AND password matches
        if (admin != null && admin.getPassword().equals(password)) {
            logger.info("Admin authenticated successfully.");
            return admin;
        }
        logger.warning("Admin authentication failed: Invalid username or password.");
        return null;
    }

    private Student authenticateStudent(String rollNumber, String password) {
        Student student = studentDAO.getStudentByRollNumber(rollNumber);
        
        // Ensure student exists, is active, AND password matches
        if (student != null && student.getPassword().equals(password)) {
            if ("Active".equalsIgnoreCase(student.getStatus())) {
                logger.info("Student authenticated successfully.");
                return student;
            } else {
                logger.warning("Student authentication failed: Account is not active.");
                return null;
            }
        }
        logger.warning("Student authentication failed: Invalid roll number or password.");
        return null;
    }

    private Faculty authenticateFaculty(String empId, String password) {
        Faculty faculty = facultyDAO.getFacultyByEmpId(empId);
        
        if (faculty != null && faculty.getPassword().equals(password)) {
            if ("Active".equalsIgnoreCase(faculty.getStatus())) {
                logger.info("Faculty authenticated successfully.");
                return faculty;
            } else {
                logger.warning("Faculty authentication failed: Account is not active.");
                return null;
            }
        }
        logger.warning("Faculty authentication failed: Invalid employee ID or password.");
        return null;
    }
    
    /**
     * Phase 5.7 Requirement: Change Password
     */
    public boolean changePassword(int userId, String newPassword, String role) {
        if (newPassword == null || newPassword.length() < 6) {
            logger.warning("Password change failed: Password must be at least 6 characters.");
            return false;
        }
        
        switch (role.toUpperCase()) {
            case "ADMIN":
                return adminDAO.updatePassword(userId, newPassword);
            case "STUDENT":
                return studentDAO.updatePassword(userId, newPassword);
            case "FACULTY":
                return facultyDAO.updatePassword(userId, newPassword);
            default:
                return false;
        }
    }
}