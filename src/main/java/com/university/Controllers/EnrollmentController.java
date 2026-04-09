package com.university.Controllers;

import com.university.services.EnrollmentService;
import com.university.models.Enrollment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EnrollmentController - REST API for Course Enrollment Management
 * Handle student enrollments in courses
 * Students view their enrolled courses
 * Faculty view enrolled students in their courses
 * Admin manages enrollments
 */
@RestController
@RequestMapping("/api/enrollment")
@CrossOrigin(origins = "*")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // ==================== CREATE ENROLLMENT ====================

    /**
     * POST /api/enrollment/enroll
     * Admin/Student enrolls a student in a course
     * 
     * @param enrollment Enrollment object with student_id, course_id, enrollment_date, status
     * @return Success/Error response
     */
    @PostMapping("/enroll")
    public ResponseEntity<Map<String, Object>> enrollStudent(@RequestBody Enrollment enrollment) {
        Map<String, Object> response = new HashMap<>();

        try {
            String validationError = enrollmentService.validateEnrollmentData(enrollment);
            if (validationError != null) {
                response.put("status", "error");
                response.put("message", validationError);
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            boolean success = enrollmentService.enrollStudent(enrollment);
            if (success) {
                response.put("status", "success");
                response.put("message", "Student enrolled successfully");
                response.put("data", enrollment);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to enroll student. Student may already be enrolled.");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error enrolling student: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== VIEW ENROLLMENTS ====================

    /**
     * GET /api/enrollment/student/{studentId}
     * Get all courses a student is enrolled in
     * Students use this to view their enrolled courses
     * 
     * @param studentId Student ID
     * @return List of enrolled courses
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<Map<String, Object>> getStudentEnrollments(@PathVariable("studentId") int studentId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(studentId);

            response.put("status", "success");
            response.put("message", "Student enrollments retrieved successfully");
            response.put("data", enrollments);
            response.put("count", enrollments.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving student enrollments: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/enrollment/course/{courseId}
     * Get all students enrolled in a specific course
     * Faculty/Admin use this to see enrolled students
     * 
     * @param courseId Course ID
     * @return List of enrolled students
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getCourseEnrollments(@PathVariable("courseId") int courseId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);

            response.put("status", "success");
            response.put("message", "Course enrollments retrieved successfully");
            response.put("data", enrollments);
            response.put("count", enrollments.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving course enrollments: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/enrollment/all
     * Get all enrollments (Admin only)
     * 
     * @return List of all enrollments
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllEnrollments() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Enrollment> enrollments = enrollmentService.getAllEnrollments();

            response.put("status", "success");
            response.put("message", "All enrollments retrieved successfully");
            response.put("data", enrollments);
            response.put("count", enrollments.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving enrollments: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== UPDATE ENROLLMENT ====================

    /**
     * PUT /api/enrollment/update/{enrollmentId}
     * Admin/Faculty updates enrollment status
     * 
     * @param enrollmentId Enrollment ID
     * @param enrollment Updated enrollment object
     * @return Success/Error response
     */
    @PutMapping("/update/{enrollmentId}")
    public ResponseEntity<Map<String, Object>> updateEnrollment(
            @PathVariable("enrollmentId") int enrollmentId,
            @RequestBody Enrollment enrollment) {
        Map<String, Object> response = new HashMap<>();

        try {
            String validationError = enrollmentService.validateEnrollmentData(enrollment);
            if (validationError != null) {
                response.put("status", "error");
                response.put("message", validationError);
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            boolean success = enrollmentService.updateEnrollment(enrollmentId, enrollment);
            if (success) {
                response.put("status", "success");
                response.put("message", "Enrollment updated successfully");
                response.put("data", enrollment);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to update enrollment");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error updating enrollment: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== DELETE ENROLLMENT ====================

    /**
     * DELETE /api/enrollment/delete/{enrollmentId}
     * Admin/Faculty removes a student from a course
     * 
     * @param enrollmentId Enrollment ID
     * @return Success/Error response
     */
    @DeleteMapping("/delete/{enrollmentId}")
    public ResponseEntity<Map<String, Object>> deleteEnrollment(@PathVariable("enrollmentId") int enrollmentId) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean success = enrollmentService.deleteEnrollment(enrollmentId);
            if (success) {
                response.put("status", "success");
                response.put("message", "Enrollment deleted successfully");
                response.put("data", null);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to delete enrollment");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error deleting enrollment: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/enrollment/status/{enrollmentId}
     * Get detailed enrollment information
     * 
     * @param enrollmentId Enrollment ID
     * @return Enrollment details
     */
    @GetMapping("/status/{enrollmentId}")
    public ResponseEntity<Map<String, Object>> getEnrollmentStatus(@PathVariable("enrollmentId") int enrollmentId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Enrollment enrollment = enrollmentService.getEnrollmentById(enrollmentId);

            if (enrollment != null) {
                response.put("status", "success");
                response.put("message", "Enrollment details retrieved successfully");
                response.put("data", enrollment);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Enrollment not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving enrollment: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
