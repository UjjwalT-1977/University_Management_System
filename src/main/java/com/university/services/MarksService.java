package com.university.services;

import com.university.dao.MarksDAO;
import com.university.dao.StudentDAO;
import com.university.dao.CourseDAO;
import com.university.models.Marks;
import com.university.models.Student;
import com.university.models.Course;

import java.util.List;
import java.util.logging.Logger;

/**
 * Business Logic Layer for Examination and Marks
 */
public class MarksService {

    private final MarksDAO marksDAO;
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    
    // We can also reference StudentService here if we want to auto-update CGPA
    private final StudentService studentService; 
    
    private static final Logger logger = Logger.getLogger(MarksService.class.getName());

    public MarksService() {
        this.marksDAO = new MarksDAO();
        this.studentDAO = new StudentDAO();
        this.courseDAO = new CourseDAO();
        this.studentService = new StudentService(); 
    }

    /**
     * Core Validation Logic for Examination Marks
     */
    public String validateMarksData(Marks marks) {
        if (marks.getInternalMarks() < 0 || marks.getInternalMarks() > 40) {
            return "Invalid Internal Marks. Must be between 0 and 40.";
        }
        if (marks.getExternalMarks() < 0 || marks.getExternalMarks() > 60) {
            return "Invalid External Marks. Must be between 0 and 60.";
        }
        // Total marks cannot exceed 100
        if ((marks.getInternalMarks() + marks.getExternalMarks()) > 100) {
            return "Total marks cannot exceed 100.";
        }
        return "VALID";
    }

    /**
     * Record new marks for a student
     */
    public boolean recordMarks(Marks marks) {
        logger.info("Attempting to record marks for Student ID: " + marks.getStudentId() + " in Course ID: " + marks.getCourseId());

        // 1. Validate raw number data
        String validationMsg = validateMarksData(marks);
        if (!validationMsg.equals("VALID")) {
            logger.warning("Record Marks failed: " + validationMsg);
            System.err.println(validationMsg);
            return false;
        }

        // 2. Business Rule: Ensure Student and Course actually exist
        if (studentDAO.getStudentById(marks.getStudentId()) == null || courseDAO.getCourseById(marks.getCourseId()) == null) {
            logger.warning("Record Marks failed: Student or Course does not exist.");
            return false;
        }

        // 3. Business Rule: Prevent Duplicate Entries (Can't grade the same student twice for one course)
        if (marksDAO.getMarksByStudentAndCourse(marks.getStudentId(), marks.getCourseId()) != null) {
            logger.warning("Record Marks failed: Marks already exist for this student in this course.");
            System.err.println("Marks already recorded. Use the 'Update' function instead.");
            return false;
        }

        // 4. Auto-calculate total and letter grade using your Model's utility method
        marks.setTotalMarks(marks.getInternalMarks() + marks.getExternalMarks());
        marks.calculateGrade(); 

        // 5. Save to database
        boolean success = marksDAO.addMarks(marks);
        
        if (success) {
            logger.info("Successfully recorded marks. Grade awarded: " + marks.getGrade());
            
            // OPTIONAL BUT AWESOME: Automatically recalculate the student's overall CGPA!
            studentService.calculateCGPA(marks.getStudentId());
        }
        
        return success;
    }

    /**
     * Update existing marks
     */
    public boolean updateMarks(Marks marks) {
        logger.info("Attempting to update marks ID: " + marks.getMarksId());

        String validationMsg = validateMarksData(marks);
        if (!validationMsg.equals("VALID")) {
            logger.warning("Update Marks failed: " + validationMsg);
            return false;
        }

        // Auto-recalculate totals and grades before saving updates
        marks.setTotalMarks(marks.getInternalMarks() + marks.getExternalMarks());
        marks.calculateGrade();

        boolean success = marksDAO.updateMarks(marks);
        
        if (success) {
            // Re-calculate the overall CGPA since a grade was changed
            studentService.calculateCGPA(marks.getStudentId());
        }
        
        return success;
    }

    /**
     * Get all marks for a specific student (Report Card Data)
     */
    public List<Marks> getStudentMarks(int studentId) {
        logger.info("Fetching all marks for Student ID: " + studentId);
        return marksDAO.getMarksByStudent(studentId);
    }

    /**
     * Get all marks for a specific course (Teacher's Ledger)
     */
    public List<Marks> getCourseMarks(int courseId) {
        logger.info("Fetching all marks for Course ID: " + courseId);
        return marksDAO.getMarksByCourse(courseId);
    }

    /**
     * Generate a Result Sheet summary for a Course
     * Phase 5.5 Requirement: Gathers data for reports
     */
    public String generateResultSheetSummary(int courseId) {
        logger.info("Generating result sheet summary for Course ID: " + courseId);
        
        Course course = courseDAO.getCourseById(courseId);
        if (course == null) return "Course not found.";

        List<String[]> gradeDistribution = marksDAO.getGradeDistribution(courseId);
        
        // Build a nice text summary (Later, ReportService can turn this into a PDF)
        StringBuilder report = new StringBuilder();
        report.append("=== RESULT SHEET ===\n");
        report.append("Course: ").append(course.getCourseCode()).append(" - ").append(course.getCourseName()).append("\n");
        report.append("--------------------\n");
        report.append("Grade Distribution:\n");
        
        int totalPassed = 0;
        int totalStudents = 0;
        
        for (String[] row : gradeDistribution) {
            String grade = row[0];
            int count = Integer.parseInt(row[1]);
            
            report.append("Grade ").append(grade).append(": ").append(count).append(" students\n");
            
            totalStudents += count;
            if (!grade.equals("F")) {
                totalPassed += count; // Anyone who didn't get an F passed
            }
        }
        
        if (totalStudents > 0) {
            double passPercentage = ((double) totalPassed / totalStudents) * 100;
            report.append("--------------------\n");
            report.append("Overall Pass Percentage: ").append(Math.round(passPercentage)).append("%\n");
        } else {
            report.append("No grades recorded yet.\n");
        }
        
        return report.toString();
    }
}