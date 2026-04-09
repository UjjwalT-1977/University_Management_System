package com.university.services;

import com.university.dao.AttendanceDAO;
import com.university.models.Attendance;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Business Logic Layer for Tracking Student Attendance
 */
@Service
public class AttendanceService {

    private final AttendanceDAO attendanceDAO;
    private static final Logger logger = Logger.getLogger(AttendanceService.class.getName());

    @Autowired
    public AttendanceService(AttendanceDAO attendanceDAO) {
        this.attendanceDAO = attendanceDAO;
    }

    // Standard University Rule: 75% attendance required to write exams
    private static final int MINIMUM_ATTENDANCE_REQUIRED = 75;

    /**
     * Core Validation Logic for Attendance Records
     */
    public String validateAttendanceData(Attendance attendance) {
        if (attendance.getDate() == null) {
            return "Attendance date cannot be null.";
        }
         
        // Prevent marking attendance for future dates
        if (attendance.getDate().isAfter(LocalDate.now())) {
            return "Cannot mark attendance for a future date.";
        }
        
        String status = attendance.getStatus();
        if (status == null || (!status.equalsIgnoreCase("Present") 
                && !status.equalsIgnoreCase("Absent") 
                && !status.equalsIgnoreCase("Leave"))) {
            return "Invalid status. Must be 'Present', 'Absent', or 'Leave'.";
        }
        
        if (attendance.getStudentId() <= 0 || attendance.getCourseId() <= 0) {
            return "Valid Student ID and Course ID are required.";
        }
        
        return "VALID";
    }

    /**
     * Mark new attendance for a student
     */
    public boolean markAttendance(Attendance attendance) {
        logger.info("Marking attendance for Student ID: " + attendance.getStudentId() + " on " + attendance.getDate());

        // 1. Validate raw data
        String validationMsg = validateAttendanceData(attendance);
        if (!validationMsg.equals("VALID")) {
            logger.warning("Attendance marking failed: " + validationMsg);
            return false;
        }

        // 2. Business Rule: Prevent Duplicate Daily Entries
        // Check if the student already has an attendance record for this exact course and date
        List<Attendance> existingRecords = attendanceDAO.getAttendanceByStudent(attendance.getStudentId());
        for (Attendance record : existingRecords) {
            if (record.getCourseId() == attendance.getCourseId() && record.getDate().equals(attendance.getDate())) {
                logger.warning("Attendance rejected: Record already exists for Student " + attendance.getStudentId() + " on this date for Course " + attendance.getCourseId());
                System.out.println("[ATTENDANCE SERVICE] Duplicate attendance detected - not inserting");
                return false;
            }
        }

        // 3. Save to Database
        System.out.println("[ATTENDANCE SERVICE] Inserting new attendance record");
        return attendanceDAO.addAttendance(attendance);
    }

    /**
     * Update an existing attendance record (e.g., changing Absent to Present)
     */
    public boolean updateAttendance(int attendanceId, Attendance attendance) {
        logger.info("Updating attendance ID: " + attendanceId + " with status: " + attendance.getStatus());
        
        String validationMsg = validateAttendanceData(attendance);
        if (!validationMsg.equals("VALID")) {
            logger.warning("Update failed: " + validationMsg);
            return false;
        }
        
        // Set the attendance ID on the object for the DAO to use
        attendance.setAttendanceId(attendanceId);
        
        return attendanceDAO.updateAttendance(attendance);
    }

    /**
     * Get a specific student's attendance history
     */
    public List<Attendance> getAttendanceReportForStudent(int studentId) {
        logger.info("Fetching attendance report for Student ID: " + studentId);
        return attendanceDAO.getAttendanceByStudent(studentId);
    }

    /**
     * Get attendance history for a whole course
     */
    public List<Attendance> getAttendanceReportForCourse(int courseId) {
        logger.info("Fetching attendance report for Course ID: " + courseId);
        return attendanceDAO.getAttendanceByCourse(courseId);
    }
    
    /**
     * Get exact percentage of attendance
     */
    public int getStudentAttendancePercentage(int studentId, int courseId) {
        return attendanceDAO.getStudentAttendancePercentage(studentId, courseId);
    }

    /**
     * Check if a student is eligible to take the final exam based on the 75% rule
     */
    public boolean isEligibleForExams(int studentId, int courseId) {
        logger.info("Checking exam eligibility for Student ID: " + studentId + " in Course ID: " + courseId);
        
        int percentage = getStudentAttendancePercentage(studentId, courseId);
        boolean isEligible = percentage >= MINIMUM_ATTENDANCE_REQUIRED;
        
        if (!isEligible) {
            logger.warning("Student " + studentId + " is NOT eligible. Current attendance: " + percentage + "%");
        } else {
            logger.info("Student " + studentId + " IS eligible. Current attendance: " + percentage + "%");
        }
        
        return isEligible;
    }

    // ==================== NEW METHODS FOR REST API ====================

    /**
     * Get attendance by student (alias for getAttendanceReportForStudent)
     */
    public List<Attendance> getAttendanceByStudent(int studentId) {
        return getAttendanceReportForStudent(studentId);
    }

    /**
     * Get attendance by course (alias for getAttendanceReportForCourse)
     */
    public List<Attendance> getAttendanceByCourse(int courseId) {
        return getAttendanceReportForCourse(courseId);
    }

    /**
     * Get attendance for a specific student in a specific course
     */
    public List<Attendance> getAttendanceByStudentAndCourse(int studentId, int courseId) {
        logger.info("Fetching attendance for Student ID: " + studentId + " in Course ID: " + courseId);
        return attendanceDAO.getAttendanceByStudentAndCourse(studentId, courseId);
    }

    /**
     * Delete attendance by ID
     */
    public boolean deleteAttendance(int attendanceId) {
        logger.info("Deleting attendance ID: " + attendanceId);
        return attendanceDAO.deleteAttendance(attendanceId);
    }
}