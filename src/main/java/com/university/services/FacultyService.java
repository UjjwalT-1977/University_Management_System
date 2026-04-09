package com.university.services;

import com.university.dao.FacultyDAO;
import com.university.dao.CourseDAO;
import com.university.models.Faculty;
import com.university.models.Course;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.logging.Logger;

/**
 * Business Logic Layer for Faculty Operations
 */
@Service
public class FacultyService {

    private final FacultyDAO facultyDAO;
    private final CourseDAO courseDAO; 
    
    private static final Logger logger = Logger.getLogger(FacultyService.class.getName());

    @Autowired
    public FacultyService(FacultyDAO facultyDAO, CourseDAO courseDAO) {
        this.facultyDAO = facultyDAO;
        this.courseDAO = courseDAO;
    }

    /**
     * Core Validation Logic - Ensures no bad data reaches the database
     */
    public String validateFacultyData(Faculty faculty) {
        if (faculty.getName() == null || faculty.getName().trim().isEmpty()) {
            return "Faculty name cannot be empty.";
        }
        if (faculty.getEmail() == null || !faculty.getEmail().contains("@")) {
            return "Invalid email format. Must contain '@'.";
        }
        if (faculty.getPhone() == null || faculty.getPhone().length() < 10) {
            return "Invalid phone number. Must be at least 10 digits.";
        }
        if (faculty.getQualification() == null || faculty.getQualification().trim().isEmpty()) {
            return "Qualification must be specified.";
        }
        if (faculty.getDeptId() <= 0) {
            return "A valid Department ID must be assigned.";
        }
        return "VALID"; 
    }

    /**
     * Add a new faculty member with business rules
     */
    public boolean addFaculty(Faculty faculty) {
        logger.info("Attempting to add new faculty: " + faculty.getName());
        
        // 1. Validate raw data
        String validationMsg = validateFacultyData(faculty);
        if (!validationMsg.equals("VALID")) {
            logger.warning("Add Faculty failed due to validation: " + validationMsg);
            System.err.println(validationMsg);
            return false;
        }

        // 2. Business Rule: Check if Employee ID already exists using your DAO method
        if (facultyDAO.getFacultyByEmpId(faculty.getEmpId()) != null) {
            logger.warning("Add Faculty failed: Employee ID '" + faculty.getEmpId() + "' already exists.");
            return false;
        }

        // 3. Save to database
        boolean success = facultyDAO.addFaculty(faculty);
        if (success) {
            logger.info("Successfully added faculty with Emp ID: " + faculty.getEmpId());
        }
        return success;
    }

    /**
     * Retrieve full faculty details by ID
     */
    public Faculty getFacultyDetails(int facultyId) {
        logger.info("Fetching details for faculty ID: " + facultyId);
        return facultyDAO.getFacultyById(facultyId);
    }

    /**
     * Update faculty information with validation
     */
    public boolean updateFacultyInfo(Faculty faculty) {
        logger.info("Attempting to update faculty ID: " + faculty.getFacultyId());
        
        // Ensure the updated info still passes university rules
        String validationMsg = validateFacultyData(faculty);
        if (!validationMsg.equals("VALID")) {
            logger.warning("Update failed due to validation: " + validationMsg);
            System.err.println(validationMsg);
            return false;
        }
        
        return facultyDAO.updateFaculty(faculty);
    }

    /**
     * Filter and get all active faculty members
     */
    public List<Faculty> getAllActiveFaculty() {
        logger.info("Fetching all active faculty members");
        // Using the exact method you wrote in FacultyDAO
        return facultyDAO.getActiveFaculty();
    }

    /**
     * Get all faculty members in the system
     */
    public List<Faculty> getAllFaculty() {
        logger.info("Fetching all faculty members");
        return facultyDAO.getAllFaculty();
    }

    /**
     * Get faculty by employee ID
     */
    public Faculty getFacultyByEmpId(String empId) {
        logger.info("Fetching faculty with emp ID: " + empId);
        return facultyDAO.getFacultyByEmpId(empId);
    }

    /**
     * Get faculty by department
     */
    public List<Faculty> getFacultyByDept(int deptId) {
        logger.info("Fetching faculty for Department ID: " + deptId);
        return facultyDAO.getFacultyByDepartment(deptId);
    }

    /**
     * Delete a faculty member from the system
     */
    public boolean deleteFaculty(int facultyId) {
        logger.info("Attempting to delete faculty ID: " + facultyId);
        return facultyDAO.deleteFaculty(facultyId);
    }

    /**
     * Get all courses currently assigned to a specific faculty member
     */
    public List<Course> getCoursesAssignedToFaculty(int facultyId) {
        logger.info("Fetching courses assigned to faculty ID: " + facultyId);
        // Using the exact method you wrote in CourseDAO
        return courseDAO.getCoursesByFaculty(facultyId);
    }
}