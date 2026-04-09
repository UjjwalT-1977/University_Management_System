package com.university.Controllers;

import com.university.services.MarksService;
import com.university.models.Marks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MarksController - REST API for Marks/Grades Management
 * Faculty can enter and update student marks
 * Students can view their marks and grades
 */
@RestController
@RequestMapping("/api/marks")
@CrossOrigin(origins = "*")
public class MarksController {

    private final MarksService marksService;

    @Autowired
    public MarksController(MarksService marksService) {
        this.marksService = marksService;
    }

    // ==================== ENTER MARKS (Faculty Only) ====================

    /**
     * POST /api/marks/enter
     * Faculty enters marks for a student in a course
     * 
     * @param marks Marks object with student_id, course_id, faculty_id, marks, grade
     * @return Success/Error response
     */
    @PostMapping("/enter")
    public ResponseEntity<Map<String, Object>> enterMarks(@RequestBody Marks marks) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("=== Marks Recording Request ===");
            System.out.println("Student ID: " + marks.getStudentId());
            System.out.println("Course ID: " + marks.getCourseId());
            System.out.println("Internal Marks: " + marks.getInternalMarks());
            System.out.println("External Marks: " + marks.getExternalMarks());
            System.out.println("Total Marks: " + marks.getTotalMarks());

            String validationError = marksService.validateMarksData(marks);
            if (!validationError.equals("VALID")) {
                System.out.println("Validation Error: " + validationError);
                response.put("status", "error");
                response.put("message", validationError);
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            boolean success = marksService.addMarks(marks);
            if (success) {
                System.out.println("Marks recorded successfully!");
                response.put("status", "success");
                response.put("message", "Marks entered successfully");
                response.put("data", marks);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                System.out.println("Failed to add marks to database");
                response.put("status", "error");
                response.put("message", "Failed to enter marks - database error");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            System.err.println("Exception in enterMarks: " + e.getMessage());
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error entering marks: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /api/marks/add
     * Alias for enterMarks - Add marks for a student in a course
     * 
     * @param marks Marks object
     * @return Success/Error response
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addMarks(@RequestBody Marks marks) {
        return enterMarks(marks);
    }

    // ==================== VIEW MARKS ====================

    /**
     * GET /api/marks/student/{studentId}
     * Get all marks for a specific student
     * 
     * @param studentId Student ID
     * @return List of marks record
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<Map<String, Object>> getStudentMarks(@PathVariable("studentId") int studentId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Marks> marksList = marksService.getMarksByStudent(studentId);

            response.put("status", "success");
            response.put("message", "Student marks retrieved successfully");
            response.put("data", marksList);
            response.put("count", marksList.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving student marks: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/marks/course/{courseId}
     * Get all marks for a specific course
     * Faculty uses this to see all marks in a course
     * 
     * @param courseId Course ID
     * @return List of marks in this course
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getCourseMarks(@PathVariable("courseId") int courseId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Marks> marksList = marksService.getMarksByCourse(courseId);

            response.put("status", "success");
            response.put("message", "Course marks retrieved successfully");
            response.put("data", marksList);
            response.put("count", marksList.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving course marks: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/marks/student/{studentId}/course/{courseId}
     * Get marks for a specific student in a specific course
     * 
     * @param studentId Student ID
     * @param courseId Course ID
     * @return Marks record
     */
    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getStudentCourseMarks(
            @PathVariable("studentId") int studentId,
            @PathVariable("courseId") int courseId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Marks marks = marksService.getMarksByStudentAndCourse(studentId, courseId);

            if (marks != null) {
                response.put("status", "success");
                response.put("message", "Marks retrieved successfully");
                response.put("data", marks);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Marks not found for this student-course combination");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving marks: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== UPDATE MARKS ====================

    /**
     * PUT /api/marks/update/{marksId}
     * Faculty updates marks for a student
     * 
     * @param marksId Marks record ID
     * @param marks Updated marks object
     * @return Success/Error response
     */
    @PutMapping("/update/{marksId}")
    public ResponseEntity<Map<String, Object>> updateMarks(
            @PathVariable("marksId") int marksId,
            @RequestBody Marks marks) {
        Map<String, Object> response = new HashMap<>();

        try {
            String validationError = marksService.validateMarksData(marks);
            if (!validationError.equals("VALID")) {
                response.put("status", "error");
                response.put("message", validationError);
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            boolean success = marksService.updateMarks(marksId, marks);
            if (success) {
                response.put("status", "success");
                response.put("message", "Marks updated successfully");
                response.put("data", marks);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to update marks");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error updating marks: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== DELETE MARKS ====================

    /**
     * DELETE /api/marks/delete/{marksId}
     * Faculty deletes a marks record
     * 
     * @param marksId Marks record ID
     * @return Success/Error response
     */
    @DeleteMapping("/delete/{marksId}")
    public ResponseEntity<Map<String, Object>> deleteMarks(@PathVariable("marksId") int marksId) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean success = marksService.deleteMarks(marksId);
            if (success) {
                response.put("status", "success");
                response.put("message", "Marks record deleted successfully");
                response.put("data", null);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to delete marks record");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error deleting marks: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/marks/cgpa/{studentId}
     * Calculate and return CGPA for a student
     * 
     * @param studentId Student ID
     * @return CGPA value
     */
    @GetMapping("/cgpa/{studentId}")
    public ResponseEntity<Map<String, Object>> calculateCGPA(@PathVariable("studentId") int studentId) {
        Map<String, Object> response = new HashMap<>();

        try {
            double cgpa = marksService.calculateCGPAForStudent(studentId);

            response.put("status", "success");
            response.put("message", "CGPA calculated successfully");
            response.put("data", cgpa);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error calculating CGPA: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
