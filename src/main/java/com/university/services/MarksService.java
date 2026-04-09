package com.university.services;

import com.university.dao.MarksDAO;
import com.university.dao.StudentDAO;
import com.university.dao.CourseDAO;
import com.university.models.Marks;
import com.university.models.Student; 
import com.university.models.Course;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.logging.Logger;

/**
 * Business Logic Layer for Examination and Marks
 */
@Service
public class MarksService {

    private final MarksDAO marksDAO;
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    
    // We can also reference StudentService here if we want to auto-update CGPA
    private final StudentService studentService; 
    
    private static final Logger logger = Logger.getLogger(MarksService.class.getName());

    @Autowired
    public MarksService(MarksDAO marksDAO, StudentDAO studentDAO, CourseDAO courseDAO, StudentService studentService) {
        this.marksDAO = marksDAO;
        this.studentDAO = studentDAO;
        this.courseDAO = courseDAO;
        this.studentService = studentService; 
    }

    /**
     * Core Validation Logic for Examination Marks
     */
    public String validateMarksData(Marks marks) {
        if (marks.getInternalMarks() < 0 || marks.getInternalMarks() > 30) {
            return "Invalid Internal Marks. Must be between 0 and 30.";
        }
        if (marks.getExternalMarks() < 0 || marks.getExternalMarks() > 70) {
            return "Invalid External Marks. Must be between 0 and 70.";
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
        System.out.println("\n=== MarksService.recordMarks() ===");
        System.out.println("Student ID: " + marks.getStudentId());
        System.out.println("Course ID: " + marks.getCourseId());
        System.out.println("Internal: " + marks.getInternalMarks() + ", External: " + marks.getExternalMarks());

        // 1. Validate raw number data
        String validationMsg = validateMarksData(marks);
        if (!validationMsg.equals("VALID")) {
            logger.warning("Record Marks failed: " + validationMsg);
            System.err.println("Validation failed: " + validationMsg);
            return false;
        }

        // 2. Business Rule: Ensure Student and Course actually exist
        boolean studentExists = studentDAO.getStudentById(marks.getStudentId()) != null;
        boolean courseExists = courseDAO.getCourseById(marks.getCourseId()) != null;
        
        System.out.println("Student Exists: " + studentExists);
        System.out.println("Course Exists: " + courseExists);
        
        if (!studentExists || !courseExists) {
            logger.warning("Record Marks failed: Student or Course does not exist.");
            System.err.println("ERROR: Student or Course not found!");
            return false;
        }

        // 3. Check if marks already exist - if yes, UPDATE instead of INSERT
        Marks existingMarks = marksDAO.getMarksByStudentAndCourse(marks.getStudentId(), marks.getCourseId());
        if (existingMarks != null) {
            System.out.println("Marks already exist (ID: " + existingMarks.getMarksId() + "). Updating instead...");
            logger.info("Marks already exist for this student-course. Updating instead of inserting.");
            marks.setMarksId(existingMarks.getMarksId());
            return updateMarks(marks);
        }

        // 4. Auto-calculate total and letter grade using your Model's utility method
        marks.setTotalMarks(marks.getInternalMarks() + marks.getExternalMarks());
        marks.calculateGrade();
        
        System.out.println("Calculated Total: " + marks.getTotalMarks() + ", Grade: " + marks.getGrade());

        // 5. Save to database
        boolean success = marksDAO.addMarks(marks);
        
        if (success) {
            logger.info("Successfully recorded marks. Grade awarded: " + marks.getGrade());
            System.out.println("SUCCESS: Marks recorded!");
            
            // OPTIONAL BUT AWESOME: Automatically recalculate the student's overall CGPA!
            studentService.calculateCGPA(marks.getStudentId());
        } else {
            System.out.println("FAILED: Could not insert marks in database");
        }
        
        return success;
    }

    /**
     * Update existing marks
     */
    public boolean updateMarks(Marks marks) {
        logger.info("Attempting to update marks ID: " + marks.getMarksId());
        System.out.println("\n=== MarksService.updateMarks() ===");
        System.out.println("Marks ID: " + marks.getMarksId());

        String validationMsg = validateMarksData(marks);
        if (!validationMsg.equals("VALID")) {
            System.out.println("Validation failed: " + validationMsg);
            logger.warning("Update Marks failed: " + validationMsg);
            return false;
        }

        // Auto-recalculate totals and grades before saving updates
        marks.setTotalMarks(marks.getInternalMarks() + marks.getExternalMarks());
        marks.calculateGrade();
        
        System.out.println("Recalculated Total: " + marks.getTotalMarks() + ", Grade: " + marks.getGrade());

        boolean success = marksDAO.updateMarks(marks);
        System.out.println("Update success: " + success);
        
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

    // ==================== NEW METHODS FOR REST API ====================

    /**
     * Add new marks (alias for recordMarks)
     */
    public boolean addMarks(Marks marks) {
        return recordMarks(marks);
    }

    /**
     * Get marks for a specific student (alias for getStudentMarks)
     */
    public List<Marks> getMarksByStudent(int studentId) {
        return getStudentMarks(studentId);
    }

    /**
     * Get marks for a specific course (alias for getCourseMarks)
     */
    public List<Marks> getMarksByCourse(int courseId) {
        return getCourseMarks(courseId);
    }

    /**
     * Get marks for a specific student in a specific course
     */
    public Marks getMarksByStudentAndCourse(int studentId, int courseId) {
        logger.info("Fetching marks for Student ID: " + studentId + " in Course ID: " + courseId);
        return marksDAO.getMarksByStudentAndCourse(studentId, courseId);
    }

    /**
     * Update marks by ID
     */
    public boolean updateMarks(int marksId, Marks marks) {
        marks.setMarksId(marksId);
        return updateMarks(marks);
    }

    /**
     * Delete marks by ID
     */
    public boolean deleteMarks(int marksId) {
        logger.info("Deleting marks ID: " + marksId);
        return marksDAO.deleteMarks(marksId);
    }

    /**
     * Calculate CGPA for a student
     */
    public double calculateCGPAForStudent(int studentId) {
        logger.info("Calculating CGPA for Student ID: " + studentId);
        List<Marks> studentMarksList = getMarksByStudent(studentId);
        
        if (studentMarksList == null || studentMarksList.isEmpty()) {
            return 0.0;
        }

        double totalGradePoints = 0;
        int totalCredits = 0;

        for (Marks marks : studentMarksList) {
            Course course = courseDAO.getCourseById(marks.getCourseId());
            if (course != null) {
                double gradePoint = convertGradeToPoint(marks.getGrade());
                totalGradePoints += gradePoint * course.getCredits();
                totalCredits += course.getCredits();
            }
        }

        if (totalCredits > 0) {
            double cgpa = totalGradePoints / totalCredits;
            Student student = studentDAO.getStudentById(studentId);
            if (student != null) {
                student.setCgpa(cgpa);
                studentDAO.updateStudent(student);
            }
            return cgpa;
        }

        return 0.0;
    }

    /**
     * Convert letter grade to grade point
     */
    private double convertGradeToPoint(String grade) {
        switch (grade) {
            case "A+":
            case "A":
                return 4.0;
            case "B+":
                return 3.5;
            case "B":
                return 3.0;
            case "C+":
                return 2.5;
            case "C":
                return 2.0;
            case "D":
                return 1.0;
            case "F":
            default:
                return 0.0;
        }
    }
}