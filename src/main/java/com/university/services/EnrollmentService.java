package com.university.services;

import com.university.dao.EnrollmentDAO;
import com.university.dao.StudentDAO;
import com.university.dao.CourseDAO;
import com.university.models.Enrollment;
import com.university.models.Course;
import com.university.models.Student;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Business Logic Layer for Student Enrollments
 */
public class EnrollmentService {

    private final EnrollmentDAO enrollmentDAO;
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    
    private static final Logger logger = Logger.getLogger(EnrollmentService.class.getName());

    public EnrollmentService() {
        this.enrollmentDAO = new EnrollmentDAO();
        this.studentDAO = new StudentDAO();
        this.courseDAO = new CourseDAO();
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
}