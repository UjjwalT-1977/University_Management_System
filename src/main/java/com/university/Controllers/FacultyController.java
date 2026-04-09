package com.university.Controllers;

import com.university.services.FacultyService;
import com.university.models.Faculty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FacultyController - REST API for Faculty Management
 * Handles CRUD operations for faculty/teacher records
 * Admin-only operations (future: add @PreAuthorize)
 */
@RestController
@RequestMapping("/api/faculty")
@CrossOrigin(origins = "*")
public class FacultyController {

    private final FacultyService facultyService;

    @Autowired
    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    // ==================== CREATE FACULTY ====================

    /**
     * POST /api/faculty/add
     * Add a new faculty member to the system
     * 
     * @param faculty Faculty object with all required fields
     * @return Success/Error response with new faculty details
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addFaculty(@RequestBody Faculty faculty) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean success = facultyService.addFaculty(faculty);

            if (success) {
                response.put("status", "success");
                response.put("message", "Faculty added successfully");
                response.put("data", faculty);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to add faculty. Check validation errors.");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error adding faculty: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== READ OPERATIONS ====================

    /**
     * GET /api/faculty/all
     * Fetch all faculty members in the system
     * 
     * @return List of all faculty members (passwords masked for security)
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllFaculty() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Faculty> faculty = facultyService.getAllFaculty();
            
            // Mask passwords before sending to frontend
            for (Faculty f : faculty) {
                f.setPassword("********");
            }

            response.put("status", "success");
            response.put("message", "Faculty retrieved successfully");
            response.put("data", faculty);
            response.put("count", faculty.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving faculty: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/faculty/{id}
     * Fetch faculty by ID
     * 
     * @param facultyId Faculty ID
     * @return Faculty object (password masked)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getFacultyById(@PathVariable("id") int facultyId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Faculty faculty = facultyService.getFacultyDetails(facultyId);

            if (faculty != null) {
                faculty.setPassword("********"); // Mask password
                response.put("status", "success");
                response.put("message", "Faculty retrieved successfully");
                response.put("data", faculty);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Faculty not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving faculty: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/faculty/empid/{empId}
     * Fetch faculty by employee ID
     * 
     * @param empId Employee ID
     * @return Faculty object (password masked)
     */
    @GetMapping("/empid/{empId}")
    public ResponseEntity<Map<String, Object>> getFacultyByEmpId(@PathVariable("empId") String empId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Faculty faculty = facultyService.getFacultyByEmpId(empId);

            if (faculty != null) {
                faculty.setPassword("********"); // Mask password
                response.put("status", "success");
                response.put("message", "Faculty retrieved successfully");
                response.put("data", faculty);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Faculty not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving faculty: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/faculty/dept/{deptId}
     * Fetch all faculty members in a specific department
     * 
     * @param deptId Department ID
     * @return List of faculty in that department
     */
    @GetMapping("/dept/{deptId}")
    public ResponseEntity<Map<String, Object>> getFacultyByDepartment(@PathVariable("deptId") int deptId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Faculty> faculty = facultyService.getFacultyByDept(deptId);

            // Mask passwords
            for (Faculty f : faculty) {
                f.setPassword("********");
            }

            response.put("status", "success");
            response.put("message", "Faculty retrieved successfully for department: " + deptId);
            response.put("data", faculty);
            response.put("count", faculty.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving faculty: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== UPDATE FACULTY ====================

    /**
     * PUT /api/faculty/update/{id}
     * Update faculty details
     * 
     * @param facultyId Faculty ID
     * @param faculty Faculty object with updated fields
     * @return Updated faculty object
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateFaculty(
            @PathVariable("id") int facultyId,
            @RequestBody Faculty faculty) {
        Map<String, Object> response = new HashMap<>();

        try {
            faculty.setFacultyId(facultyId); // Ensure we're updating the right faculty

            // Check if faculty exists
            Faculty existingFaculty = facultyService.getFacultyDetails(facultyId);
            if (existingFaculty == null) {
                response.put("status", "error");
                response.put("message", "Faculty not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Call service to update
            boolean success = facultyService.updateFacultyInfo(faculty);

            if (success) {
                faculty.setPassword("********"); // Mask password
                response.put("status", "success");
                response.put("message", "Faculty updated successfully");
                response.put("data", faculty);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to update faculty");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error updating faculty: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== DELETE FACULTY ====================

    /**
     * DELETE /api/faculty/delete/{id}
     * Delete a faculty member from the system
     * 
     * @param facultyId Faculty ID to delete
     * @return Success/Error response
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteFaculty(@PathVariable("id") int facultyId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Check if faculty exists
            Faculty faculty = facultyService.getFacultyDetails(facultyId);
            if (faculty == null) {
                response.put("status", "error");
                response.put("message", "Faculty not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Try to delete
            boolean success = facultyService.deleteFaculty(facultyId);

            if (success) {
                response.put("status", "success");
                response.put("message", "Faculty deleted successfully");
                response.put("data", null);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to delete faculty");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error deleting faculty: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
