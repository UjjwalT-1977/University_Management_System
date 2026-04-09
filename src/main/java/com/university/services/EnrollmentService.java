package com.university.services;

import com.university.dao.EnrollmentDAO;
import com.university.dao.StudentDAO;
import com.university.dao.CourseDAO;
import com.university.models.Enrollment;
import com.university.models.Course;
import com.university.models.Student;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Business Logic Layer for Student Enrollments
 */
@Service
public class EnrollmentService {

    private final EnrollmentDAO enrollmentDAO;
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    
    private static final Logger logger = Logger.getLogger(EnrollmentService.class.getName());

    @Autowired
    public EnrollmentService(EnrollmentDAO enrollmentDAO, StudentDAO studentDAO, CourseDAO courseDAO) {
        this.enrollmentDAO = enrollmentDAO;
        this.studentDAO = studentDAO;
        this.courseDAO = courseDAO;
    }

    /**
     * Core validation for capacity - Phase 5.4 specific requirement
     * Checks if a course has available seats.
     */
    public boolean validateEnrollment(int courseId) {
        Course course = courseDAO.getCourseById(courseId);
        if (course == null) {
            logger.warning("Capacity check failed: Course not found.");
            return false;
        }

        // Gets the exact count of CURRENTLY enrolled students from your DAO
        int currentCount = enrollmentDAO.getEnrollmentCountForCourse(courseId);
        
        return currentCount < course.getMaxCapacity();
    }

    /**
     * Enroll a student in a course with full business rules
     */
    public boolean enrollStudentInCourse(int studentId, int courseId) {
        logger.info("Attempting to enroll Student ID " + studentId + " into Course ID " + courseId);

        // Rule 1: Validate Student exists
        Student student = studentDAO.getStudentById(studentId);
        if (student == null) {
            logger.warning("Enrollment failed: Student not found.");
            return false;
        }

        // Rule 2: Validate Course exists
        Course course = courseDAO.getCourseById(courseId);
        if (course == null) {
            logger.warning("Enrollment failed: Course not found.");
            return false;
        }

        // Rule 3: Check Capacity limit
        if (!validateEnrollment(courseId)) {
            logger.warning("Enrollment failed: Course ID " + courseId + " is full.");
            System.err.println("Cannot enroll: The course has reached maximum capacity.");
            return false;
        }

        // Rule 4: Check if already enrolled (Prevent duplicates)
        List<Enrollment> existingEnrollments = enrollmentDAO.getEnrollmentsByStudent(studentId);
        for (Enrollment e : existingEnrollments) {
            if (e.getCourseId() == courseId && "Enrolled".equals(e.getStatus())) {
                logger.warning("Enrollment failed: Student is already enrolled in this course.");
                return false;
            }
        }

        // All rules passed - Create the Enrollment object
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setStudentId(studentId);
        newEnrollment.setCourseId(courseId);
        newEnrollment.setEnrollmentDate(LocalDate.now()); // Explicitly set in Service
        newEnrollment.setCgpaAtEnrollment(student.getCgpa()); 
        newEnrollment.setStatus("Enrolled"); // Explicitly set in Service

        // Save to Database
        boolean success = enrollmentDAO.addEnrollment(newEnrollment);
        
        if (success) {
            // Update the course's current_enrollment count in the course table
            int newCount = enrollmentDAO.getEnrollmentCountForCourse(courseId);
            courseDAO.updateEnrollmentCount(courseId, newCount);
            
            logger.info("Successfully enrolled Student ID " + studentId + " in Course ID " + courseId);
        }
        
        return success;
    }

    /**
     * Drop a student from a course (Changes status, doesn't delete record)
     */
    public boolean dropStudent(int enrollmentId) {
        logger.info("Attempting to drop enrollment ID: " + enrollmentId);
        
        Enrollment enrollment = enrollmentDAO.getEnrollmentById(enrollmentId);
        if (enrollment == null) {
            logger.warning("Drop failed: Enrollment record not found.");
            return false;
        }

        // Update status to 'Dropped' using your DAO method
        boolean success = enrollmentDAO.updateEnrollmentStatus(enrollmentId, "Dropped");
        
        if (success) {
            // Free up a seat in the course by recalculating active enrollments
            int courseId = enrollment.getCourseId();
            int updatedCount = enrollmentDAO.getEnrollmentCountForCourse(courseId);
            courseDAO.updateEnrollmentCount(courseId, updatedCount);
            
            logger.info("Successfully dropped enrollment ID: " + enrollmentId);
        }
        
        return success;
    }

    /**
     * Get all courses a specific student is enrolled in
     */
    public List<Enrollment> getEnrolledCourses(int studentId) {
        logger.info("Fetching enrollments for student ID: " + studentId);
        return enrollmentDAO.getEnrollmentsByStudent(studentId);
    }

    /**
     * Get all students enrolled in a specific course
     */
    public List<Enrollment> getEnrolledStudents(int courseId) {
        logger.info("Fetching enrollments for course ID: " + courseId);
        return enrollmentDAO.getEnrollmentsByCourse(courseId);
    }

    /**
     * Validate enrollment data from Controller requests
     */
    public String validateEnrollmentData(Enrollment enrollment) {
        if (enrollment == null) {
            return "Enrollment data cannot be null";
        }
        if (enrollment.getStudentId() <= 0) {
            return "Invalid student ID";
        }
        if (enrollment.getCourseId() <= 0) {
            return "Invalid course ID";
        }
        if (enrollment.getStatus() == null || enrollment.getStatus().isEmpty()) {
            return "Enrollment status cannot be empty";
        }
        return null; // Valid
    }

    /**
     * Enroll student using Enrollment object (wrapper for enrollStudentInCourse)
     */
    public boolean enrollStudent(Enrollment enrollment) {
        if (enrollment == null) {
            logger.warning("Enrollment object is null");
            return false;
        }
        return enrollStudentInCourse(enrollment.getStudentId(), enrollment.getCourseId());
    }

    /**
     * Get all enrollments for a specific student
     */
    public List<Enrollment> getEnrollmentsByStudent(int studentId) {
        logger.info("Fetching all enrollments for student ID: " + studentId);
        return enrollmentDAO.getEnrollmentsByStudent(studentId);
    }

    /**
     * Get all enrollments for a specific course
     */
    public List<Enrollment> getEnrollmentsByCourse(int courseId) {
        logger.info("Fetching all enrollments for course ID: " + courseId);
        return enrollmentDAO.getEnrollmentsByCourse(courseId);
    }

    /**
     * Get all enrollments in the system (Admin only)
     */
    public List<Enrollment> getAllEnrollments() {
        logger.info("Fetching all enrollments in the system");
        return enrollmentDAO.getAllEnrollments();
    }

    /**
     * Get enrollment by ID
     */
    public Enrollment getEnrollmentById(int enrollmentId) {
        logger.info("Fetching enrollment ID: " + enrollmentId);
        return enrollmentDAO.getEnrollmentById(enrollmentId);
    }

    /**
     * Update enrollment details
     */
    public boolean updateEnrollment(int enrollmentId, Enrollment enrollment) {
        logger.info("Updating enrollment ID: " + enrollmentId);
        
        Enrollment existing = enrollmentDAO.getEnrollmentById(enrollmentId);
        if (existing == null) {
            logger.warning("Enrollment not found for ID: " + enrollmentId);
            return false;
        }

        // Update the enrollment status
        return enrollmentDAO.updateEnrollmentStatus(enrollmentId, enrollment.getStatus());
    }

    /**
     * Delete enrollment record
     */
    public boolean deleteEnrollment(int enrollmentId) {
        logger.info("Deleting enrollment ID: " + enrollmentId);
        
        Enrollment enrollment = enrollmentDAO.getEnrollmentById(enrollmentId);
        if (enrollment == null) {
            logger.warning("Enrollment not found for ID: " + enrollmentId);
            return false;
        }

        boolean success = enrollmentDAO.deleteEnrollment(enrollmentId);
        
        if (success) {
            // Recalculate enrollment count for the course
            int courseId = enrollment.getCourseId();
            int updatedCount = enrollmentDAO.getEnrollmentCountForCourse(courseId);
            courseDAO.updateEnrollmentCount(courseId, updatedCount);
            logger.info("Successfully deleted enrollment ID: " + enrollmentId);
        }
        
        return success;
    }
}