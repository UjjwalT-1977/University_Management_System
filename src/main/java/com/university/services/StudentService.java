package com.university.services;

import com.university.dao.StudentDAO;
import com.university.dao.MarksDAO;
import com.university.models.Student;
import com.university.models.Marks;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.logging.Logger;

/**
 * Business Logic Layer for Student Operations
 */
@Service
public class StudentService {

    private final StudentDAO studentDAO;
    private final MarksDAO marksDAO; 
    
    private static final Logger logger = Logger.getLogger(StudentService.class.getName());

    @Autowired
    public StudentService(StudentDAO studentDAO, MarksDAO marksDAO) {
        this.studentDAO = studentDAO;
        this.marksDAO = marksDAO;
    }

    /**
     * Core Validation Logic - The "Bouncer"
     */
    public String validateStudentData(Student student) {
        if (student == null) {
            return "Student data is required.";
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            return "Student name cannot be empty.";
        }
        if (student.getRollNumber() == null || student.getRollNumber().trim().isEmpty()) {
            return "Roll number is required.";
        }
        if (student.getEmail() == null || !student.getEmail().contains("@")) {
            return "Invalid email format.";
        }
        if (student.getPhone() == null || student.getPhone().replaceAll("\\D", "").length() < 10) {
            return "Invalid phone number. Must be at least 10 digits.";
        }
        if (student.getDeptId() <= 0) {
            return "A valid Department ID is required.";
        }
        if (student.getPassword() == null || student.getPassword().trim().isEmpty()) {
            return "Password is required.";
        }
        if (student.getGender() == null || student.getGender().trim().isEmpty()) {
            return "Gender is required.";
        }
        if (!student.getGender().equals("Male") && !student.getGender().equals("Female") && !student.getGender().equals("Other")) {
            return "Gender must be Male, Female, or Other.";
        }
        if (student.getAdmissionDate() == null) {
            return "Admission date is required.";
        }
        if (student.getAdmissionDate().isAfter(LocalDate.now())) {
            return "Admission date cannot be in the future.";
        }
        if (student.getDateOfBirth() == null) {
            return "Date of birth is required.";
        }
        if (student.getDateOfBirth().isAfter(LocalDate.now())) {
            return "Date of birth cannot be in the future.";
        }
        int age = Period.between(student.getDateOfBirth(), LocalDate.now()).getYears();
        if (age < 16) {
            return "Student must be at least 16 years old to register.";
        }
        if (student.getAdmissionDate().isBefore(student.getDateOfBirth())) {
            return "Admission date cannot be before date of birth.";
        }
        if (student.getSemester() <= 0) {
            return "Semester must be a positive number.";
        }
        if (student.getCgpa() < 0.0 || student.getCgpa() > 4.0) {
            return "CGPA must be between 0.0 and 4.0.";
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
     * Get all students in the system
     */
    public List<Student> getAllStudents() {
        logger.info("Fetching all students");
        return studentDAO.getAllStudents();
    }

    /**
     * Get student by roll number
     */
    public Student getStudentByRollNumber(String rollNumber) {
        logger.info("Fetching student with roll number: " + rollNumber);
        return studentDAO.getStudentByRollNumber(rollNumber);
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
     * Delete a student from the system
     */
    public boolean deleteStudent(int studentId) {
        logger.info("Attempting to delete student ID: " + studentId);
        return studentDAO.deleteStudent(studentId);
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