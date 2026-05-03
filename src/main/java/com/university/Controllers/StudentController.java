package com.university.Controllers;

import com.university.services.StudentService;
import com.university.models.Student;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StudentController - REST API for Student Management
 * Handles CRUD operations for student records
 * Admin-only operations (future: add @PreAuthorize)
 */
@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // ==================== CREATE STUDENT ====================

    /**
     * POST /api/student/add
     * Add a new student to the system
     * 
     * @param student Student object with all required fields
     * @return Success/Error response with new student details
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addStudent(@RequestBody Student student) {
        Map<String, Object> response = new HashMap<>();

        try {
            String validationMsg = studentService.validateStudentData(student);
            if (!"VALID".equals(validationMsg)) {
                response.put("status", "error");
                response.put("message", validationMsg);
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (studentService.getStudentByRollNumber(student.getRollNumber()) != null) {
                response.put("status", "error");
                response.put("message", "Roll number already exists.");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            boolean success = studentService.registerStudent(student);

            if (success) {
                response.put("status", "success");
                response.put("message", "Student added successfully");
                response.put("data", student);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to add student. Check validation errors or database constraints.");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error adding student: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== READ OPERATIONS ====================

    /**
     * GET /api/student/all
     * Fetch all students in the system
     * 
     * @return List of all students (passwords masked for security)
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllStudents() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Student> students = studentService.getAllStudents();
            
            // Mask passwords before sending to frontend
            for (Student student : students) {
                student.setPassword("********");
            }

            response.put("status", "success");
            response.put("message", "Students retrieved successfully");
            response.put("data", students);
            response.put("count", students.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving students: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/student/{id}
     * Fetch student by ID
     * 
     * @param studentId Student ID
     * @return Student object (password masked)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getStudentById(@PathVariable("id") int studentId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Student student = studentService.getStudentDetails(studentId);

            if (student != null) {
                student.setPassword("********"); // Mask password
                response.put("status", "success");
                response.put("message", "Student retrieved successfully");
                response.put("data", student);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Student not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving student: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/student/rollnumber/{rollNumber}
     * Fetch student by roll number
     * 
     * @param rollNumber Student roll number
     * @return Student object (password masked)
     */
    @GetMapping("/rollnumber/{rollNumber}")
    public ResponseEntity<Map<String, Object>> getStudentByRollNumber(@PathVariable("rollNumber") String rollNumber) {
        Map<String, Object> response = new HashMap<>();

        try {
            Student student = studentService.getStudentByRollNumber(rollNumber);

            if (student != null) {
                student.setPassword("********"); // Mask password
                response.put("status", "success");
                response.put("message", "Student retrieved successfully");
                response.put("data", student);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Student not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving student: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/student/dept/{deptId}
     * Fetch all students in a specific department
     * 
     * @param deptId Department ID
     * @return List of students in that department
     */
    @GetMapping("/dept/{deptId}")
    public ResponseEntity<Map<String, Object>> getStudentsByDepartment(@PathVariable("deptId") int deptId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Student> students = studentService.getStudentsByDept(deptId);

            // Mask passwords
            for (Student student : students) {
                student.setPassword("********");
            }

            response.put("status", "success");
            response.put("message", "Students retrieved successfully for department: " + deptId);
            response.put("data", students);
            response.put("count", students.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving students: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== UPDATE STUDENT ====================

    /**
     * PUT /api/student/update/{id}
     * Update student details
     * 
     * @param studentId Student ID
     * @param student Student object with updated fields
     * @return Updated student object
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateStudent(
            @PathVariable("id") int studentId,
            @RequestBody Student student) {
        Map<String, Object> response = new HashMap<>();

        try {
            student.setStudentId(studentId); // Ensure we're updating the right student

            // Check if student exists
            Student existingStudent = studentService.getStudentDetails(studentId);
            if (existingStudent == null) {
                response.put("status", "error");
                response.put("message", "Student not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Call service to update
            boolean success = studentService.updateStudentInfo(student);

            if (success) {
                student.setPassword("********"); // Mask password
                response.put("status", "success");
                response.put("message", "Student updated successfully");
                response.put("data", student);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to update student");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error updating student: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== DELETE STUDENT ====================

    /**
     * DELETE /api/student/delete/{id}
     * Delete a student from the system
     * 
     * @param studentId Student ID to delete
     * @return Success/Error response
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteStudent(@PathVariable("id") int studentId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Check if student exists
            Student student = studentService.getStudentDetails(studentId);
            if (student == null) {
                response.put("status", "error");
                response.put("message", "Student not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Try to delete
            boolean success = studentService.deleteStudent(studentId);

            if (success) {
                response.put("status", "success");
                response.put("message", "Student deleted successfully");
                response.put("data", null);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to delete student");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error deleting student: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
