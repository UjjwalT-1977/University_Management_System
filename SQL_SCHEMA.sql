-- ============================================
-- UNIVERSITY MANAGEMENT SYSTEM DATABASE SCHEMA
-- ============================================

-- Step 1: Create Database
CREATE DATABASE IF NOT EXISTS university_ms;
USE university_ms;

-- ============================================
-- TABLE 1: ADMIN
-- ============================================
CREATE TABLE admin (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================
-- TABLE 2: DEPARTMENT
-- ============================================
CREATE TABLE department (
    dept_id INT PRIMARY KEY AUTO_INCREMENT,
    dept_name VARCHAR(100) NOT NULL UNIQUE,
    dept_code VARCHAR(10) UNIQUE NOT NULL,
    hod_name VARCHAR(100),
    phone VARCHAR(15),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================
-- TABLE 3: FACULTY
-- ============================================
CREATE TABLE faculty (
    faculty_id INT PRIMARY KEY AUTO_INCREMENT,
    emp_id VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    dept_id INT NOT NULL,
    password VARCHAR(255) NOT NULL,
    qualification VARCHAR(100),
    specialization VARCHAR(100),
    date_of_joining DATE,
    status ENUM('Active', 'Inactive', 'Leave') DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dept_id) REFERENCES department(dept_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================
-- TABLE 4: COURSE
-- ============================================
CREATE TABLE course (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_code VARCHAR(10) UNIQUE NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    dept_id INT NOT NULL,
    faculty_id INT,
    credits INT DEFAULT 3,
    max_capacity INT DEFAULT 60,
    current_enrollment INT DEFAULT 0,
    semester INT DEFAULT 1,
    year INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dept_id) REFERENCES department(dept_id),
    FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================
-- TABLE 5: STUDENT
-- ============================================
CREATE TABLE student (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    roll_number VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    date_of_birth DATE,
    gender ENUM('Male', 'Female', 'Other'),
    dept_id INT NOT NULL,
    password VARCHAR(255) NOT NULL,
    admission_date DATE NOT NULL,
    semester INT DEFAULT 1,
    status ENUM('Active', 'Inactive', 'Graduated') DEFAULT 'Active',
    cgpa DECIMAL(3,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dept_id) REFERENCES department(dept_id)
) ENGINE=InnoDB;

-- ============================================
-- TABLE 6: ENROLLMENT
-- ============================================
CREATE TABLE enrollment (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    enrollment_date DATE DEFAULT (CURDATE()),
    cgpa_at_enrollment DECIMAL(3,2),
    status ENUM('Enrolled', 'Dropped', 'Completed') DEFAULT 'Enrolled',
    UNIQUE KEY unique_enrollment (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================
-- TABLE 7: ATTENDANCE
-- ============================================
CREATE TABLE attendance (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    date DATE NOT NULL,
    status ENUM('Present', 'Absent', 'Leave') DEFAULT 'Present',
    recorded_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES faculty(faculty_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================
-- TABLE 8: MARKS
-- ============================================
CREATE TABLE marks (
    marks_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    internal_marks DECIMAL(5,2) DEFAULT 0,
    external_marks DECIMAL(5,2) DEFAULT 0,
    total_marks DECIMAL(5,2) DEFAULT 0,
    grade CHAR(1),
    recorded_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_marks (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES faculty(faculty_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================
-- INDEXES FOR OPTIMIZATION
-- ============================================
CREATE INDEX idx_student_dept ON student(dept_id);
CREATE INDEX idx_student_status ON student(status);
CREATE INDEX idx_student_roll ON student(roll_number);
CREATE INDEX idx_faculty_dept ON faculty(dept_id);
CREATE INDEX idx_faculty_email ON faculty(email);
CREATE INDEX idx_course_dept ON course(dept_id);
CREATE INDEX idx_course_code ON course(course_code);
CREATE INDEX idx_marks_student ON marks(student_id);
CREATE INDEX idx_marks_course ON marks(course_id);
CREATE INDEX idx_marks_grade ON marks(grade);
CREATE INDEX idx_attendance_student ON attendance(student_id);
CREATE INDEX idx_attendance_course ON attendance(course_id);
CREATE INDEX idx_attendance_date ON attendance(date);
CREATE INDEX idx_enrollment_student ON enrollment(student_id);
CREATE INDEX idx_enrollment_course ON enrollment(course_id);

-- ============================================
-- INSERT DEFAULT ADMIN USER
-- ============================================
INSERT INTO admin (username, password, email, name, phone) 
VALUES ('admin', 'admin@123', 'admin@university.edu', 'System Administrator', '1234567890');

-- ============================================
-- INSERT SAMPLE DEPARTMENTS
-- ============================================
INSERT INTO department (dept_name, dept_code, hod_name, email, phone) 
VALUES 
    ('Computer Science and Engineering', 'CSE', 'Dr. Ramesh Kumar', 'hod.cse@university.edu', '9876543210'),
    ('Electronics and Communication Engineering', 'ECE', 'Dr. Priya Singh', 'hod.ece@university.edu', '9876543211'),
    ('Mechanical Engineering', 'ME', 'Dr. Arun Patel', 'hod.me@university.edu', '9876543212'),
    ('Civil Engineering', 'CE', 'Dr. Rekha Sharma', 'hod.ce@university.edu', '9876543213');

-- ============================================
-- INSERT SAMPLE FACULTY
-- ============================================
INSERT INTO faculty (emp_id, name, email, phone, dept_id, password, qualification, specialization, date_of_joining, status) 
VALUES 
    ('CSE001', 'Prof. Sharma', 'sharma@university.edu', '9876543220', 1, 'prof@123', 'M.Tech', 'Data Science', '2015-06-15', 'Active'),
    ('CSE002', 'Prof. Desai', 'desai@university.edu', '9876543221', 1, 'prof@123', 'M.Tech', 'Web Development', '2016-07-20', 'Active'),
    ('ECE001', 'Prof. Singh', 'singh@university.edu', '9876543222', 2, 'prof@123', 'M.Tech', 'Embedded Systems', '2014-08-10', 'Active'),
    ('ME001', 'Prof. Verma', 'verma@university.edu', '9876543223', 3, 'prof@123', 'M.Tech', 'CAD/CAM', '2017-05-25', 'Active'),
    ('CE001', 'Prof. Kumar', 'kumar@university.edu', '9876543224', 4, 'prof@123', 'M.Tech', 'Structural Analysis', '2016-03-15', 'Active');

-- ============================================
-- INSERT SAMPLE COURSES
-- ============================================
INSERT INTO course (course_code, course_name, dept_id, faculty_id, credits, max_capacity, semester, year) 
VALUES 
    ('CS101', 'Data Structures', 1, 1, 4, 60, 2, 1),
    ('CS102', 'Database Management System', 1, 1, 4, 60, 2, 1),
    ('CS201', 'Web Development', 1, 2, 4, 60, 2, 2),
    ('CS202', 'Software Engineering', 1, 2, 4, 60, 2, 2),
    ('EC101', 'Digital Systems', 2, 3, 4, 60, 2, 1),
    ('EC102', 'Microelectronics', 2, 3, 4, 60, 2, 1),
    ('ME101', 'Thermodynamics', 3, 4, 4, 60, 2, 1),
    ('ME102', 'Mechanics of Materials', 3, 4, 4, 60, 2, 1),
    ('CE101', 'Structural Analysis', 4, 5, 4, 60, 2, 1),
    ('CE102', 'Building Planning', 4, 5, 4, 60, 2, 1);

-- ============================================
-- INSERT SAMPLE STUDENTS
-- ============================================
INSERT INTO student (roll_number, name, email, phone, date_of_birth, gender, dept_id, password, admission_date, semester, status, cgpa) 
VALUES 
    ('CSE001', 'Ravi Kumar', 'ravi@university.edu', '9123456789', '2004-05-15', 'Male', 1, 'student@123', '2022-08-01', 2, 'Active', 3.75),
    ('CSE002', 'Priya Sharma', 'priya@university.edu', '9123456790', '2004-03-20', 'Female', 1, 'student@123', '2022-08-01', 2, 'Active', 3.90),
    ('CSE003', 'Vikram Singh', 'vikram@university.edu', '9123456791', '2004-07-10', 'Male', 1, 'student@123', '2022-08-01', 2, 'Active', 3.45),
    ('CSE004', 'Anjali Patel', 'anjali@university.edu', '9123456792', '2004-02-25', 'Female', 1, 'student@123', '2022-08-01', 2, 'Active', 3.85),
    ('CSE005', 'Rohan Gupta', 'rohan@university.edu', '9123456793', '2004-11-12', 'Male', 1, 'student@123', '2022-08-01', 2, 'Active', 3.60),
    ('ECE001', 'Neha Verma', 'neha@university.edu', '9123456794', '2004-06-08', 'Female', 2, 'student@123', '2022-08-01', 2, 'Active', 3.70),
    ('ECE002', 'Arjun Das', 'arjun@university.edu', '9123456795', '2004-09-14', 'Male', 2, 'student@123', '2022-08-01', 2, 'Active', 3.55),
    ('ME001', 'Shruti Mishra', 'shruti@university.edu', '9123456796', '2004-04-22', 'Female', 3, 'student@123', '2022-08-01', 2, 'Active', 3.65),
    ('ME002', 'Aditya Reddy', 'aditya@university.edu', '9123456797', '2004-08-30', 'Male', 3, 'student@123', '2022-08-01', 2, 'Active', 3.40),
    ('CE001', 'Divya Nair', 'divya@university.edu', '9123456798', '2004-01-18', 'Female', 4, 'student@123', '2022-08-01', 2, 'Active', 3.80);

-- ============================================
-- INSERT SAMPLE ENROLLMENTS
-- ============================================
INSERT INTO enrollment (student_id, course_id, status) 
VALUES 
    (1, 1, 'Enrolled'), (1, 2, 'Enrolled'), (1, 3, 'Enrolled'),
    (2, 1, 'Enrolled'), (2, 2, 'Enrolled'), (2, 3, 'Enrolled'),
    (3, 1, 'Enrolled'), (3, 2, 'Enrolled'),
    (4, 1, 'Enrolled'), (4, 2, 'Enrolled'), (4, 3, 'Enrolled'),
    (5, 1, 'Enrolled'), (5, 2, 'Enrolled'),
    (6, 5, 'Enrolled'), (6, 6, 'Enrolled'),
    (7, 5, 'Enrolled'), (7, 6, 'Enrolled'),
    (8, 7, 'Enrolled'), (8, 8, 'Enrolled'),
    (9, 7, 'Enrolled'), (9, 8, 'Enrolled'),
    (10, 9, 'Enrolled'), (10, 10, 'Enrolled');

-- ============================================
-- INSERT SAMPLE MARKS
-- ============================================
INSERT INTO marks (student_id, course_id, internal_marks, external_marks, total_marks, grade, recorded_by) 
VALUES 
    (1, 1, 25, 65, 90, 'A', 1), (1, 2, 24, 63, 87, 'A', 1), (1, 3, 23, 60, 83, 'B', 2),
    (2, 1, 25, 68, 93, 'A', 1), (2, 2, 25, 70, 95, 'A', 1), (2, 3, 24, 65, 89, 'A', 2),
    (3, 1, 20, 58, 78, 'B', 1), (3, 2, 19, 55, 74, 'C', 1),
    (4, 1, 24, 66, 90, 'A', 1), (4, 2, 23, 64, 87, 'A', 1), (4, 3, 22, 61, 83, 'B', 2),
    (5, 1, 21, 60, 81, 'B', 1), (5, 2, 20, 58, 78, 'B', 1),
    (6, 5, 23, 62, 85, 'B', 3), (6, 6, 22, 60, 82, 'B', 3),
    (7, 5, 24, 64, 88, 'A', 3), (7, 6, 23, 63, 86, 'A', 3),
    (8, 7, 22, 61, 83, 'B', 4), (8, 8, 21, 59, 80, 'B', 4),
    (9, 7, 19, 55, 74, 'C', 4), (9, 8, 18, 52, 70, 'C', 4),
    (10, 9, 24, 65, 89, 'A', 5), (10, 10, 23, 63, 86, 'A', 5);

-- ============================================
-- INSERT SAMPLE ATTENDANCE (Last 30 days)
-- ============================================
INSERT INTO attendance (student_id, course_id, date, status, recorded_by) 
VALUES 
    (1, 1, '2024-10-01', 'Present', 1), (1, 1, '2024-10-02', 'Present', 1), (1, 1, '2024-10-03', 'Absent', 1),
    (1, 1, '2024-10-04', 'Present', 1), (1, 1, '2024-10-05', 'Present', 1),
    (2, 1, '2024-10-01', 'Present', 1), (2, 1, '2024-10-02', 'Present', 1), (2, 1, '2024-10-03', 'Present', 1),
    (2, 1, '2024-10-04', 'Leave', 1), (2, 1, '2024-10-05', 'Present', 1),
    (3, 1, '2024-10-01', 'Present', 1), (3, 1, '2024-10-02', 'Absent', 1), (3, 1, '2024-10-03', 'Absent', 1),
    (3, 1, '2024-10-04', 'Present', 1), (3, 1, '2024-10-05', 'Present', 1);

-- ============================================
-- VERIFICATION QUERIES
-- ============================================
-- Verify data insertion
SELECT COUNT(*) as admin_count FROM admin;
SELECT COUNT(*) as department_count FROM department;
SELECT COUNT(*) as faculty_count FROM faculty;
SELECT COUNT(*) as course_count FROM course;
SELECT COUNT(*) as student_count FROM student;
SELECT COUNT(*) as enrollment_count FROM enrollment;
SELECT COUNT(*) as marks_count FROM marks;
SELECT COUNT(*) as attendance_count FROM attendance;

-- Show sample data
SELECT * FROM student LIMIT 5;
SELECT * FROM faculty LIMIT 5;
SELECT * FROM course LIMIT 5;
