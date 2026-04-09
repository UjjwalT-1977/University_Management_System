package com.university.Controllers;

import com.university.services.AdminService;
import com.university.models.Admin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminController - REST API for Admin Management
 * Handles CRUD operations for admin accounts
 * Only accessible by authenticated admins (future: add @PreAuthorize)
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ==================== CREATE ADMIN ====================

    /**
     * POST /api/admin/add
     * Add a new admin to the system
     * 
     * @param admin Admin object with username, password, email, name, phone
     * @return Success/Error response with new admin details
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addAdmin(@RequestBody Admin admin) {
        Map<String, Object> response = new HashMap<>();

        // Call service layer (which has validation + business rules)
        boolean success = adminService.registerNewAdmin(admin);

        if (success) {
            response.put("status", "success");
            response.put("message", "Admin added successfully");
            response.put("data", admin);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("status", "error");
            response.put("message", "Failed to add admin. Check validation errors.");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ==================== READ OPERATIONS ====================

    /**
     * GET /api/admin/all
     * Fetch all admins in the system
     * 
     * @return List of all admins (passwords masked for security)
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllAdmins() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Admin> admins = adminService.getAllAdmins();
            
            // Mask passwords before sending to frontend
            for (Admin admin : admins) {
                admin.setPassword("********");
            }

            response.put("status", "success");
            response.put("message", "Admins retrieved successfully");
            response.put("data", admins);
            response.put("count", admins.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving admins: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/admin/{id}
     * Fetch admin by ID
     * 
     * @param adminId Admin ID
     * @return Admin object (password masked)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAdminById(@PathVariable("id") int adminId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Admin admin = adminService.getAdminById(adminId);

            if (admin != null) {
                admin.setPassword("********"); // Mask password
                response.put("status", "success");
                response.put("message", "Admin retrieved successfully");
                response.put("data", admin);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Admin not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving admin: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/admin/username/{username}
     * Fetch admin by username
     * 
     * @param username Admin username
     * @return Admin object (password masked)
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<Map<String, Object>> getAdminByUsername(@PathVariable("username") String username) {
        Map<String, Object> response = new HashMap<>();

        try {
            Admin admin = adminService.getAdminProfile(username);

            if (admin != null) {
                response.put("status", "success");
                response.put("message", "Admin retrieved successfully");
                response.put("data", admin);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Admin not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving admin: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== UPDATE ADMIN ====================

    /**
     * PUT /api/admin/update/{id}
     * Update admin details (email, name, phone)
     * Note: Username cannot be changed (unique identifier)
     * 
     * @param adminId Admin ID
     * @param admin Admin object with updated fields
     * @return Updated admin object
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateAdmin(
            @PathVariable("id") int adminId,
            @RequestBody Admin admin) {
        Map<String, Object> response = new HashMap<>();

        try {
            admin.setAdminId(adminId); // Ensure we're updating the right admin

            // Check if admin exists
            Admin existingAdmin = adminService.getAdminById(adminId);
            if (existingAdmin == null) {
                response.put("status", "error");
                response.put("message", "Admin not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Call service to update
            boolean success = adminService.updateAdminProfile(admin);

            if (success) {
                admin.setPassword("********"); // Mask password
                response.put("status", "success");
                response.put("message", "Admin updated successfully");
                response.put("data", admin);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to update admin");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error updating admin: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PUT /api/admin/change-password/{id}
     * Change admin password
     * 
     * @param adminId Admin ID
     * @param passwordData Map with "newPassword" field
     * @return Success/Error response
     */
    @PutMapping("/change-password/{id}")
    public ResponseEntity<Map<String, Object>> changePassword(
            @PathVariable("id") int adminId,
            @RequestBody Map<String, String> passwordData) {
        Map<String, Object> response = new HashMap<>();

        String newPassword = passwordData.get("newPassword");

        if (newPassword == null || newPassword.isEmpty()) {
            response.put("status", "error");
            response.put("message", "New password cannot be empty");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            boolean success = adminService.changeAdminPassword(adminId, newPassword);

            if (success) {
                response.put("status", "success");
                response.put("message", "Password changed successfully");
                response.put("data", null);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to change password. Password may not meet requirements.");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error changing password: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== DELETE ADMIN ====================

    /**
     * DELETE /api/admin/delete/{id}
     * Delete an admin from the system
     * Business Rule: Cannot delete if it's the last admin
     * 
     * @param adminId Admin ID to delete
     * @return Success/Error response
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteAdmin(@PathVariable("id") int adminId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Check if admin exists
            Admin admin = adminService.getAdminById(adminId);
            if (admin == null) {
                response.put("status", "error");
                response.put("message", "Admin not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Try to delete
            boolean success = adminService.deleteAdmin(adminId);

            if (success) {
                response.put("status", "success");
                response.put("message", "Admin deleted successfully");
                response.put("data", null);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to delete admin. May be the last admin in system.");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error deleting admin: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
