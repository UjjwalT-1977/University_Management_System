package com.university.services;

import com.university.dao.AdminDAO;
import com.university.models.Admin;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.logging.Logger;

/**
 * Business Logic Layer for Admin Profile Management
 */
@Service
public class AdminService {

    private final AdminDAO adminDAO;
    private static final Logger logger = Logger.getLogger(AdminService.class.getName());

    @Autowired
    public AdminService(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
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

    /**
     * Get admin by ID
     */
    public Admin getAdminById(int adminId) {
        logger.info("Fetching admin with ID: " + adminId);
        return adminDAO.getAdminById(adminId);
    }

    /**
     * Get all admins in the system
     */
    public List<Admin> getAllAdmins() {
        logger.info("Fetching all admins from the system");
        return adminDAO.getAllAdmins();
    }

    /**
     * Update admin profile (email, name, phone)
     * Note: Username and password are NOT updated here
     */
    public boolean updateAdminProfile(Admin admin) {
        logger.info("Attempting to update admin profile for Admin ID: " + admin.getAdminId());
        
        // Validate updated data
        String validationMsg = validateAdminData(admin);
        if (!validationMsg.equals("VALID")) {
            logger.warning("Profile update failed due to validation: " + validationMsg);
            return false;
        }

        return adminDAO.updateAdmin(admin);
    }

    /**
     * Delete an admin from the system
     * Business Rule: Cannot delete if it's the last admin
     */
    public boolean deleteAdmin(int adminId) {
        logger.info("Attempting to delete admin with ID: " + adminId);
        
        // Business rule: ensure at least one admin remains in the system
        int adminCount = adminDAO.countAdmins();
        if (adminCount <= 1) {
            logger.warning("Cannot delete admin. Must have at least one admin in the system.");
            return false;
        }

        return adminDAO.deleteAdmin(adminId);
    }
}