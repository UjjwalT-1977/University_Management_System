package com.university.services;

import com.university.dao.StudentDAO;
import com.university.dao.MarksDAO;
import com.university.models.Student;
import com.university.models.Marks;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.logging.Logger;

/**
 * Business Logic Layer for Student Operations
 */
public class StudentService {

    private final StudentDAO studentDAO;
    private final MarksDAO marksDAO; 
    
    private static final Logger logger = Logger.getLogger(StudentService.class.getName());

    public StudentService() {
        this.studentDAO = new StudentDAO();
        this.marksDAO = new MarksDAO(); // Initialized the MarksDAO
    }

    /**
     * Core Validation Logic - The "Bouncer"
     */
    public String validateStudentData(Student student) {
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            return "Student name cannot be empty.";
        }
        if (student.getEmail() == null || !student.getEmail().contains("@")) {
            return "Invalid email format.";
        }
        if (student.getPhone() == null || student.getPhone().length() < 10) {
            return "Invalid phone number. Must be at least 10 digits.";
        }
        if (student.getDateOfBirth() != null) {
            int age = Period.between(student.getDateOfBirth(), LocalDate.now()).getYears();
            if (age < 16) {
                return "Student must be at least 16 years old to register.";
            }
        }
        return "VALID"; 
    }

    /**
     * Register a new student with full validation
     */
    public boolean registerStudent(Student student) {
        logger.info("Attempting to register new student: " + student.getName());
        
        String validationMsg = validateStudentData(student);
        if (!validationMsg.equals("VALID")) {
            logger.warning("Registration failed due to validation: " + validationMsg);
            System.err.println(validationMsg); 
            return false;
        }

        if (studentDAO.getStudentByRollNumber(student.getRollNumber()) != null) {
            logger.warning("Registration failed: Roll number already exists.");
            return false;
        }

        boolean success = studentDAO.addStudent(student);
        if (success) {
            logger.info("Successfully registered student with Roll No: " + student.getRollNumber());
        }
        return success;
    }

    /**
     * Retrieve full student info
     */
    public Student getStudentDetails(int studentId) {
        logger.info("Fetching details for student ID: " + studentId);
        return studentDAO.getStudentById(studentId);
    }

    /**
     * Update student with validation
     */
    public boolean updateStudentInfo(Student student) {
        logger.info("Attempting to update student ID: " + student.getStudentId());
        
        String validationMsg = validateStudentData(student);
        if (!validationMsg.equals("VALID")) {
            logger.warning("Update failed due to validation: " + validationMsg);
            return false;
        }
        
        return studentDAO.updateStudent(student);
    }

    /**
     * Filter active students
     */
    public List<Student> getAllActiveStudents() {
        logger.info("Fetching all active students");
        return studentDAO.getStudentsByStatus("Active");
    }

    /**
     * Get students by department
     */
    public List<Student> getStudentsByDept(int deptId) {
        logger.info("Fetching students for Department ID: " + deptId);
        return studentDAO.getStudentsByDepartment(deptId);
    }

    /**
     * Calculate CGPA based on all recorded marks
     * Uses a standard 10-point college scale: A=10, B=9, C=8, D=7, F=0
     */
    public double calculateCGPA(int studentId) {
        logger.info("Calculating CGPA for student ID: " + studentId);
        
        // 1. Fetch all marks for this student using the DAO you provided
        List<Marks> allMarks = marksDAO.getMarksByStudent(studentId);
        
        // 2. If they have no marks yet, their CGPA is 0.0
        if (allMarks == null || allMarks.isEmpty()) {
            return 0.0;
        }
        
        // 3. Convert grades to points
        double totalGradePoints = 0.0;
        for (Marks mark : allMarks) {
            totalGradePoints += convertGradeToPoints(mark.getGrade());
        }
        
        // 4. Calculate the average points
        double calculatedCgpa = totalGradePoints / allMarks.size();
        
        // Round to 2 decimal places (e.g., 8.567 -> 8.57)
        calculatedCgpa = Math.round(calculatedCgpa * 100.0) / 100.0;
        
        // 5. Save the newly calculated CGPA to the database
        studentDAO.updateCGPA(studentId, calculatedCgpa);
        
        logger.info("Student " + studentId + " CGPA updated to: " + calculatedCgpa);
        return calculatedCgpa;
    }

    /**
     * Helper Method: Maps letter grades to 10-point scale
     */
    private double convertGradeToPoints(String grade) {
        if (grade == null) return 0.0;
        
        switch (grade.toUpperCase()) {
            case "A": return 10.0;
            case "B": return 9.0;
            case "C": return 8.0;
            case "D": return 7.0;
            default: return 0.0; // "F" gets 0 points
        }
    }
}