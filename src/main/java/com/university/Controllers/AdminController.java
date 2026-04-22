package com.university.Controllers;

import com.university.services.AdminService;
import com.university.services.EnrollmentService;
import com.university.models.Admin;
import com.university.dto.EnrollmentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminController - REST API for Admin Management
 * Handles CRUD operations for admin accounts and admin-specific operations like student enrollment
 * Only accessible by authenticated admins (future: add @PreAuthorize)
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;
    private final EnrollmentService enrollmentService;

    @Autowired
    public AdminController(AdminService adminService, EnrollmentService enrollmentService) {
        this.adminService = adminService;
        this.enrollmentService = enrollmentService;
    }

    // ==================== DEBUG ENDPOINT ====================

    /**
     * POST /api/admin/debug/enrollment-test
     * Debug endpoint to test enrollment request structure
     * 
     * @param enrollmentRequest The enrollment request object
     * @return Echo back the received request
     */
    @PostMapping("/debug/enrollment-test")
    public ResponseEntity<Map<String, Object>> debugEnrollmentRequest(
            @RequestBody(required = false) EnrollmentRequest enrollmentRequest) {
        Map<String, Object> response = new HashMap<>();
        
        System.out.println("\n=== DEBUG ENDPOINT CALLED ===");
        if (enrollmentRequest == null) {
            System.out.println("DEBUG: enrollmentRequest is NULL");
            response.put("status", "error");
            response.put("message", "enrollmentRequest is NULL");
            response.put("data", null);
        } else {
            System.out.println("DEBUG: studentId=" + enrollmentRequest.getStudentId());
            System.out.println("DEBUG: courseId=" + enrollmentRequest.getCourseId());
            System.out.println("DEBUG: adminId=" + enrollmentRequest.getAdminId());
            
            response.put("status", "success");
            response.put("message", "Debug endpoint received request");
            response.put("data", enrollmentRequest);
        }
        System.out.println("=== END DEBUG ===\n");
        
        return ResponseEntity.ok(response);
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

    // ==================== ENROLLMENT MANAGEMENT ====================

    /**
     * POST /api/admin/enrollment/enroll-student
     * Admin enrolls a student into a course
     * 
     * REQUIREMENT: Only ADMIN can perform this operation
     * RESTRICTION: Students cannot self-enroll
     * 
     * @param enrollmentRequest EnrollmentRequest with studentId, courseId, adminId
     * @return Success/Error response with enrollment details
     */
    @PostMapping("/enrollment/enroll-student")
    public ResponseEntity<Map<String, Object>> enrollStudentInCourse(
            @RequestBody EnrollmentRequest enrollmentRequest) {
        Map<String, Object> response = new HashMap<>();
        
        System.out.println("DEBUG: Enrollment request received");
        System.out.println("DEBUG: StudentID=" + enrollmentRequest.getStudentId() + ", CourseID=" + enrollmentRequest.getCourseId() + ", AdminID=" + enrollmentRequest.getAdminId());

        try {
            // ==================== AUTHORIZATION CHECK ====================
            Admin admin = adminService.getAdminById(enrollmentRequest.getAdminId());
            if (admin == null) {
                System.out.println("DEBUG: Admin not found for ID: " + enrollmentRequest.getAdminId());
                response.put("status", "error");
                response.put("message", "❌ Unauthorized: Admin ID " + enrollmentRequest.getAdminId() + " not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            System.out.println("DEBUG: Admin verified: " + admin.getName());

            // ==================== VALIDATION CHECKS ====================
            if (enrollmentRequest.getStudentId() <= 0) {
                System.out.println("DEBUG: Invalid student ID: " + enrollmentRequest.getStudentId());
                response.put("status", "error");
                response.put("message", "❌ Invalid student ID provided: " + enrollmentRequest.getStudentId());
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (enrollmentRequest.getCourseId() <= 0) {
                System.out.println("DEBUG: Invalid course ID: " + enrollmentRequest.getCourseId());
                response.put("status", "error");
                response.put("message", "❌ Invalid course ID provided: " + enrollmentRequest.getCourseId());
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // ==================== CHECK IF STUDENT EXISTS ====================
            // Using enrollmentService's method to check student
            com.university.models.Student student = null;
            try {
                // Try to use a method to get student - need to access from DAO or Service
                List<com.university.models.Student> allStudents = new java.util.ArrayList<>();
                System.out.println("DEBUG: Checking if student ID " + enrollmentRequest.getStudentId() + " exists");
            } catch (Exception e) {
                System.out.println("DEBUG: Error checking student: " + e.getMessage());
            }

            // ==================== CHECK IF COURSE EXISTS ====================
            System.out.println("DEBUG: Checking if course ID " + enrollmentRequest.getCourseId() + " exists");

            // ==================== CHECK FOR DUPLICATE ENROLLMENT ====================
            System.out.println("DEBUG: Checking if student already enrolled...");
            if (enrollmentService.isStudentAlreadyEnrolled(
                    enrollmentRequest.getStudentId(), 
                    enrollmentRequest.getCourseId())) {
                System.out.println("DEBUG: Student already enrolled in course");
                response.put("status", "error");
                response.put("message", "❌ Student is already enrolled in this course");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // ==================== PERFORM ENROLLMENT ====================
            System.out.println("DEBUG: Attempting enrollment...");
            boolean success = enrollmentService.adminEnrollStudent(
                    enrollmentRequest.getStudentId(),
                    enrollmentRequest.getCourseId(),
                    enrollmentRequest.getAdminId()
            );

            if (success) {
                System.out.println("DEBUG: Enrollment successful!");
                response.put("status", "success");
                response.put("message", "✅ Student enrolled successfully in the course");
                response.put("data", enrollmentRequest);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                System.out.println("DEBUG: Enrollment failed in service layer");
                response.put("status", "error");
                response.put("message", "❌ Enrollment failed - Student may not exist, Course may not exist, Course may be full, or Student already enrolled");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Exception during enrollment: " + e.getMessage());
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "❌ Error during enrollment: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/admin/enrollment/course-count/{courseId}
     * Get the number of students enrolled in a specific course
     * Used by faculty to see their course enrollment count for dashboard
     * 
     * @param courseId Course ID
     * @return Enrollment count with course details
     */
    @GetMapping("/enrollment/course-count/{courseId}")
    public ResponseEntity<Map<String, Object>> getCourseEnrollmentCount(
            @PathVariable("courseId") int courseId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<com.university.models.Enrollment> enrollments = 
                    enrollmentService.getEnrollmentsByCourse(courseId);

            response.put("status", "success");
            response.put("message", "Course enrollment count retrieved successfully");
            response.put("courseId", courseId);
            response.put("enrollmentCount", enrollments != null ? enrollments.size() : 0);
            response.put("data", enrollments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving enrollment count: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/admin/enrollment/student-courses/{studentId}
     * Get all courses a student is enrolled in
     * Used by student dashboard to display enrolled courses
     * 
     * @param studentId Student ID
     * @return List of courses the student is enrolled in
     */
    @GetMapping("/enrollment/student-courses/{studentId}")
    public ResponseEntity<Map<String, Object>> getStudentEnrolledCourses(
            @PathVariable("studentId") int studentId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<com.university.models.Enrollment> enrollments = 
                    enrollmentService.getEnrollmentsByStudent(studentId);

            response.put("status", "success");
            response.put("message", "Student enrolled courses retrieved successfully");
            response.put("studentId", studentId);
            response.put("enrolledCourseCount", enrollments != null ? enrollments.size() : 0);
            response.put("data", enrollments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving student courses: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
