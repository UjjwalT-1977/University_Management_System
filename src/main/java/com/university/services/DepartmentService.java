package com.university.services;

import com.university.dao.DepartmentDAO;
import com.university.models.Department;

import java.util.List;
import java.util.logging.Logger;

/**
 * Business Logic Layer for Department Operations
 */
public class DepartmentService {

    private final DepartmentDAO departmentDAO;
    private static final Logger logger = Logger.getLogger(DepartmentService.class.getName());

    public DepartmentService() {
        this.departmentDAO = new DepartmentDAO();
    }

    /**
     * Core Validation Logic for Department Data
     */
    public String validateDepartmentData(Department dept) {
        if (dept.getDeptName() == null || dept.getDeptName().trim().isEmpty()) {
            return "Department name cannot be empty.";
        }
        if (dept.getDeptCode() == null || dept.getDeptCode().trim().isEmpty()) {
            return "Department code cannot be empty.";
        }
        return "VALID";
    }

    /**
     * Add a new department with validation and duplicate checking
     */
    public boolean addDepartment(Department dept) {
        logger.info("Attempting to add new department: " + dept.getDeptName());

        // 1. Validate raw data
        String validationMsg = validateDepartmentData(dept);
        if (!validationMsg.equals("VALID")) {
            logger.warning("Add Department failed due to validation: " + validationMsg);
            System.err.println(validationMsg);
            return false;
        }

        // 2. Business Rule: Prevent duplicate department codes
        if (departmentDAO.getDepartmentByCode(dept.getDeptCode()) != null) {
            logger.warning("Add Department failed: Department Code '" + dept.getDeptCode() + "' already exists.");
            System.err.println("A department with this code already exists.");
            return false;
        }

        // 3. Save to database
        boolean success = departmentDAO.addDepartment(dept);
        if (success) {
            logger.info("Successfully added department: " + dept.getDeptCode());
        }
        return success;
    }

    /**
     * Update an existing department's information
     */
    public boolean updateDepartment(Department dept) {
        logger.info("Attempting to update department ID: " + dept.getDeptId());

        String validationMsg = validateDepartmentData(dept);
        if (!validationMsg.equals("VALID")) {
            logger.warning("Update Department failed due to validation: " + validationMsg);
            return false;
        }

        // Notice based on your DAO: updateDepartment does NOT change the dept_code.
        // It only updates name, hod, phone, and email.
        return departmentDAO.updateDepartment(dept);
    }

    /**
     * Delete a department
     */
    public boolean deleteDepartment(int deptId) {
        logger.info("Attempting to delete department ID: " + deptId);
        // Note: In a real system, you might want a business rule here to check if 
        // there are active students/faculty in this department before allowing deletion!
        return departmentDAO.deleteDepartment(deptId);
    }

    /**
     * Retrieve all departments (Crucial for UI dropdown menus)
     */
    public List<Department> getAllDepartments() {
        logger.info("Fetching all departments");
        return departmentDAO.getAllDepartments();
    }

    /**
     * Fetch specific department details by ID
     */
    public Department getDepartmentDetails(int deptId) {
        logger.info("Fetching details for department ID: " + deptId);
        return departmentDAO.getDepartmentById(deptId);
    }
    
    /**
     * Fetch specific department details by Code
     */
    public Department getDepartmentByCode(String deptCode) {
        logger.info("Fetching details for department code: " + deptCode);
        return departmentDAO.getDepartmentByCode(deptCode);
    }
}