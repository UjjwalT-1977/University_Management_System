package com.university.Controllers;

import com.university.services.DepartmentService;
import com.university.models.Department;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DepartmentController - REST API for Department Management
 * Handles CRUD operations for departments
 * Accessible by authenticated admins
 */
@RestController
@RequestMapping("/api/department")
@CrossOrigin(origins = "*")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    // ==================== ADD DEPARTMENT ====================

    /**
     * POST /api/department/add
     * Add a new department
     * 
     * @param department Department object with deptName, deptCode, hodName, phone, email
     * @return Success/Error response
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addDepartment(@RequestBody Department department) {
        Map<String, Object> response = new HashMap<>();

        try {
            String validationError = departmentService.validateDepartmentData(department);
            if (!validationError.equals("VALID")) {
                response.put("status", "error");
                response.put("message", validationError);
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            boolean success = departmentService.addDepartment(department);
            if (success) {
                response.put("status", "success");
                response.put("message", "Department added successfully");
                response.put("data", department);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to add department. Department code may already exist.");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error adding department: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== VIEW DEPARTMENTS ====================

    /**
     * GET /api/department/all
     * Get all departments in the system
     * 
     * @return List of all departments
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllDepartments() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Department> departments = departmentService.getAllDepartments();
            response.put("status", "success");
            response.put("message", "Departments retrieved successfully");
            response.put("data", departments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving departments: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/department/{deptId}
     * Get a specific department by ID
     * 
     * @param deptId The department ID
     * @return Department details
     */
    @GetMapping("/{deptId}")
    public ResponseEntity<Map<String, Object>> getDepartmentById(@PathVariable int deptId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Department department = departmentService.getDepartmentDetails(deptId);
            if (department != null) {
                response.put("status", "success");
                response.put("message", "Department retrieved successfully");
                response.put("data", department);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Department not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving department: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== UPDATE DEPARTMENT ====================

    /**
     * PUT /api/department/update/{deptId}
     * Update department information
     * 
     * @param deptId The department ID
     * @param department Updated department data
     * @return Success/Error response
     */
    @PutMapping("/update/{deptId}")
    public ResponseEntity<Map<String, Object>> updateDepartment(
            @PathVariable int deptId,
            @RequestBody Department department) {
        Map<String, Object> response = new HashMap<>();

        try {
            department.setDeptId(deptId);
            
            String validationError = departmentService.validateDepartmentData(department);
            if (!validationError.equals("VALID")) {
                response.put("status", "error");
                response.put("message", validationError);
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            boolean success = departmentService.updateDepartment(department);
            if (success) {
                response.put("status", "success");
                response.put("message", "Department updated successfully");
                response.put("data", department);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to update department");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error updating department: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== DELETE DEPARTMENT ====================

    /**
     * DELETE /api/department/delete/{deptId}
     * Delete a department
     * 
     * @param deptId The department ID to delete
     * @return Success/Error response
     */
    @DeleteMapping("/delete/{deptId}")
    public ResponseEntity<Map<String, Object>> deleteDepartment(@PathVariable int deptId) {
        Map<String, Object> response = new HashMap<>() ;

        try {
            boolean success = departmentService.deleteDepartment(deptId);
            if (success) {
                response.put("status", "success");
                response.put("message", "Department deleted successfully");
                response.put("data", null);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to delete department. It may have associated courses.");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error deleting department: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
