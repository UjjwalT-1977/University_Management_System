package com.university.services;

import com.university.dao.CourseDAO;
import com.university.dao.FacultyDAO;
import com.university.models.Course;

import java.util.logging.Logger;

/**
 * Business Logic Layer for Academic Course Operations
 */
public class CourseService {

    private final CourseDAO courseDAO;
    private final FacultyDAO facultyDAO; // Used to validate if a teacher exists before assigning
    
    private static final Logger logger = Logger.getLogger(CourseService.class.getName());

    public CourseService() {
        this.courseDAO = new CourseDAO();
        this.facultyDAO = new FacultyDAO();
    }

    /**
     * Core Validation Logic for Courses
     */
    public String validateCourseData(Course course) {
        if (course.getCourseCode() == null || course.getCourseCode().trim().isEmpty()) {
            return "Course code cannot be empty.";
        }
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            return "Course name cannot be empty.";
        }
        if (course.getCredits() <= 0 || course.getCredits() > 6) {
            return "Invalid credits. Usually must be between 1 and 6.";
        }
        if (course.getMaxCapacity() <= 0) {
            return "Maximum capacity must be greater than 0.";
        }
        if (course.getDeptId() <= 0) {
            return "A valid Department ID must be assigned.";
        }
        return "VALID";
    }

    /**
     * Create a new course with validation
     */
    public boolean createCourse(Course course) {
        logger.info("Attempting to create new course: " + course.getCourseCode());
        
        // 1. Validate raw data
        String validationMsg = validateCourseData(course);
        if (!validationMsg.equals("VALID")) {
            logger.warning("Course creation failed due to validation: " + validationMsg);
            System.err.println(validationMsg);
            return false;
        }

        // 2. Business Rule: Check if Course Code already exists
        if (courseDAO.getCourseByCourseCode(course.getCourseCode()) != null) {
            logger.warning("Course creation failed: Course Code '" + course.getCourseCode() + "' already exists.");
            return false;
        }

        // 3. Save to database
        boolean success = courseDAO.addCourse(course);
        if (success) {
            logger.info("Successfully created course: " + course.getCourseCode());
        }
        return success;
    }

    /**
     * Retrieve full course details by ID
     */
    public Course getCourseDetails(int courseId) {
        logger.info("Fetching details for course ID: " + courseId);
        return courseDAO.getCourseById(courseId);
    }

    /**
     * Update course information with validation
     */
    public boolean updateCourse(Course course) {
        logger.info("Attempting to update course ID: " + course.getCourseId());
        
        String validationMsg = validateCourseData(course);
        if (!validationMsg.equals("VALID")) {
            logger.warning("Update failed due to validation: " + validationMsg);
            System.err.println(validationMsg);
            return false;
        }
        
        return courseDAO.updateCourse(course);
    }

    /**
     * Delete a course
     */
    public boolean deleteCourse(int courseId) {
        logger.info("Attempting to delete course ID: " + courseId);
        return courseDAO.deleteCourse(courseId);
    }

    /**
     * Check if a course has available seats for enrollment
     * Returns true if there is space, false if it is full.
     */
    public boolean checkCapacity(int courseId) {
        logger.info("Checking capacity for course ID: " + courseId);
        
        Course course = courseDAO.getCourseById(courseId);
        if (course == null) {
            logger.warning("Capacity check failed: Course not found.");
            return false; // Can't enroll in a course that doesn't exist
        }
        
        // Business Rule: Current enrollment must be strictly less than maximum capacity
        boolean hasSpace = course.getCurrentEnrollment() < course.getMaxCapacity();
        
        if (!hasSpace) {
            logger.info("Course ID " + courseId + " is at maximum capacity (" + course.getMaxCapacity() + ").");
        }
        
        return hasSpace;
    }

    /**
     * Assign a faculty member to teach a specific course
     */
    public boolean assignFacultyToCourse(int courseId, int facultyId) {
        logger.info("Attempting to assign Faculty ID " + facultyId + " to Course ID " + courseId);
        
        // 1. Check if the course exists
        Course course = courseDAO.getCourseById(courseId);
        if (course == null) {
            logger.warning("Assignment failed: Course not found.");
            return false;
        }
        
        // 2. Check if the faculty exists (using FacultyDAO)
        if (facultyDAO.getFacultyById(facultyId) == null) {
            logger.warning("Assignment failed: Faculty member not found.");
            return false;
        }
        
        // 3. Update the course object with the new teacher
        course.setFacultyId(facultyId);
        
        // 4. Save the update to the database
        boolean success = courseDAO.updateCourse(course);
        if (success) {
            logger.info("Successfully assigned Faculty ID " + facultyId + " to Course ID " + courseId);
        }
        
        return success;
    }
}