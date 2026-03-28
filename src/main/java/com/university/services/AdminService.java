package com.university.services;

import com.university.dao.AdminDAO;
import com.university.models.Admin;

import java.util.logging.Logger;

/**
 * Business Logic Layer for Admin Profile Management
 */
public class AdminService {

    private final AdminDAO adminDAO;
    private static final Logger logger = Logger.getLogger(AdminService.class.getName());

    public AdminService() {
        this.adminDAO = new AdminDAO();
    }

    /**
     * Core Validation Logic for Admins
     */
    public String validateAdminData(Admin admin) {
        if (admin.getUsername() == null || admin.getUsername().trim().isEmpty()) {
            return "Username cannot be empty.";
        }
        if (admin.getPassword() == null || admin.getPassword().length() < 6) {
            return "Password must be at least 6 characters long.";
        }
        if (admin.getEmail() == null || !admin.getEmail().contains("@")) {
            return "Invalid email format.";
        }
        if (admin.getName() == null || admin.getName().trim().isEmpty()) {
            return "Name cannot be empty.";
        }
        return "VALID";
    }

    /**
     * Register a new Admin in the system
     */
    public boolean registerNewAdmin(Admin admin) {
        logger.info("Attempting to register new admin username: " + admin.getUsername());

        // 1. Validate raw data
        String validationMsg = validateAdminData(admin);
        if (!validationMsg.equals("VALID")) {
            logger.warning("Admin registration failed: " + validationMsg);
            System.err.println(validationMsg);
            return false;
        }

        // 2. Business Rule: Prevent duplicate admin usernames
        if (adminDAO.getAdminByUsername(admin.getUsername()) != null) {
            logger.warning("Admin registration failed: Username already exists.");
            return false;
        }

        // 3. Save to database
        boolean success = adminDAO.addAdmin(admin); 
        if (success) {
            logger.info("Successfully registered new admin: " + admin.getUsername());
        }
        return success;
    }

    /**
     * Fetch an Admin's profile details safely
     */
    public Admin getAdminProfile(String username) {
        logger.info("Fetching profile for admin: " + username);
        Admin admin = adminDAO.getAdminByUsername(username);
        
        if (admin != null) {
            // SECURITY BEST PRACTICE: Never send the actual password back to the UI!
            admin.setPassword("********"); 
        }
        return admin;
    }

    /**
     * Update an Admin's password with validation
     */
    public boolean changeAdminPassword(int adminId, String newPassword) {
        logger.info("Attempting to change password for Admin ID: " + adminId);
        
        if (newPassword == null || newPassword.length() < 6) {
            logger.warning("Password change failed: Password must be at least 6 characters.");
            return false;
        }
        
        return adminDAO.updatePassword(adminId, newPassword);
    }
}