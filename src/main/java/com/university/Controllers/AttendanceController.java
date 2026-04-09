package com.university.Controllers;

import com.university.services.AttendanceService;
import com.university.models.Attendance;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AttendanceController - REST API for Attendance Management
 * Faculty can mark attendance for students
 * Students can view their attendance records
 */
@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    // ==================== MARK ATTENDANCE (Faculty Only) ====================

    /**
     * POST /api/attendance/mark
     * Faculty marks attendance for a student in a course
     * 
     * @param attendance Attendance object with student_id, course_id, faculty_id, status
     * @return Success/Error response
     */
    @PostMapping("/mark")
    public ResponseEntity<Map<String, Object>> markAttendance(@RequestBody Attendance attendance) {
        Map<String, Object> response = new HashMap<>();

        System.out.println("[ATTENDANCE CONTROLLER] Marking new attendance");
        System.out.println("[ATTENDANCE CONTROLLER] Student ID: " + attendance.getStudentId() + 
                         ", Course ID: " + attendance.getCourseId() + 
                         ", Date: " + attendance.getDate() + 
                         ", Status: " + attendance.getStatus() +
                         ", Recorded By: " + attendance.getRecordedBy());

        try {
            String validationError = attendanceService.validateAttendanceData(attendance);
            System.out.println("[ATTENDANCE CONTROLLER] Validation result: " + validationError);
            
            if (!validationError.equals("VALID")) {
                System.out.println("[ATTENDANCE CONTROLLER] Validation failed: " + validationError);
                response.put("status", "error");
                response.put("message", validationError);
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            boolean success = attendanceService.markAttendance(attendance);
            System.out.println("[ATTENDANCE CONTROLLER] Mark success: " + success);
            
            if (success) {
                response.put("status", "success");
                response.put("message", "Attendance marked successfully");
                response.put("data", attendance);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to mark attendance");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            System.err.println("[ATTENDANCE CONTROLLER] Error marking attendance: " + e.getMessage());
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error marking attendance: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== VIEW ATTENDANCE ====================

    /**
     * GET /api/attendance/student/{studentId}
     * Get all attendance records for a specific student
     * 
     * @param studentId Student ID
     * @return List of attendance records
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<Map<String, Object>> getStudentAttendance(@PathVariable("studentId") int studentId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Attendance> records = attendanceService.getAttendanceByStudent(studentId);

            response.put("status", "success");
            response.put("message", "Attendance records retrieved successfully");
            response.put("data", records);
            response.put("count", records.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving attendance records: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/attendance/course/{courseId}
     * Get all attendance records for a specific course
     * Faculty uses this to see all attendance in a course
     * 
     * @param courseId Course ID
     * @return List of attendance records in this course
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getCourseAttendance(@PathVariable("courseId") int courseId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Attendance> records = attendanceService.getAttendanceByCourse(courseId);

            response.put("status", "success");
            response.put("message", "Course attendance records retrieved successfully");
            response.put("data", records);
            response.put("count", records.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving course attendance: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/attendance/student/{studentId}/course/{courseId}
     * Get attendance for a specific student in a specific course
     * 
     * @param studentId Student ID
     * @param courseId Course ID
     * @return Attendance percentage or records
     */
    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getStudentCourseAttendance(
            @PathVariable("studentId") int studentId,
            @PathVariable("courseId") int courseId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Attendance> records = attendanceService.getAttendanceByStudentAndCourse(studentId, courseId);

            response.put("status", "success");
            response.put("message", "Student course attendance retrieved successfully");
            response.put("data", records);
            response.put("count", records.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving attendance: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== UPDATE ATTENDANCE ====================

    /**
     * PUT /api/attendance/update/{attendanceId}
     * Faculty updates attendance record (correct if marked wrong)
     * 
     * @param attendanceId Attendance record ID
     * @param attendance Updated attendance object
     * @return Success/Error response
     */
    @PutMapping("/update/{attendanceId}")
    public ResponseEntity<Map<String, Object>> updateAttendance(
            @PathVariable("attendanceId") int attendanceId,
            @RequestBody Attendance attendance) {
        Map<String, Object> response = new HashMap<>();

        System.out.println("[ATTENDANCE CONTROLLER] Attempting to update attendance ID: " + attendanceId);
        System.out.println("[ATTENDANCE CONTROLLER] Update data - Student ID: " + attendance.getStudentId() + 
                         ", Course ID: " + attendance.getCourseId() + 
                         ", Date: " + attendance.getDate() + 
                         ", Status: " + attendance.getStatus());

        try {
            String validationError = attendanceService.validateAttendanceData(attendance);
            System.out.println("[ATTENDANCE CONTROLLER] Validation result: " + validationError);
            
            if (!validationError.equals("VALID")) {
                System.out.println("[ATTENDANCE CONTROLLER] Validation failed: " + validationError);
                response.put("status", "error");
                response.put("message", validationError);
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            boolean success = attendanceService.updateAttendance(attendanceId, attendance);
            System.out.println("[ATTENDANCE CONTROLLER] Update success: " + success);
            
            if (success) {
                response.put("status", "success");
                response.put("message", "Attendance updated successfully");
                response.put("data", attendance);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to update attendance");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            System.err.println("[ATTENDANCE CONTROLLER] Error updating attendance: " + e.getMessage());
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error updating attendance: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== DELETE ATTENDANCE ====================

    /**
     * DELETE /api/attendance/delete/{attendanceId}
     * Faculty deletes an attendance record
     * 
     * @param attendanceId Attendance record ID
     * @return Success/Error response
     */
    @DeleteMapping("/delete/{attendanceId}")
    public ResponseEntity<Map<String, Object>> deleteAttendance(@PathVariable("attendanceId") int attendanceId) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean success = attendanceService.deleteAttendance(attendanceId);
            if (success) {
                response.put("status", "success");
                response.put("message", "Attendance record deleted successfully");
                response.put("data", null);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to delete attendance record");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error deleting attendance: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
