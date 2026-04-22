package com.university.Controllers;

import com.university.services.CourseService;
import com.university.models.Course;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CourseController - REST API for Course Management
 * Handle course creation, updates, and retrieval
 * Admin manages all courses
 * Faculty view courses they teach
 * Students view available and enrolled courses
 */
@RestController
@RequestMapping("/api/course")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // ==================== CREATE COURSE ====================

    /**
     * POST /api/course/add
     * Admin adds a new course
     * 
     * @param course Course object with course_code, course_name, dept_id, faculty_id, credits
     * @return Success/Error response
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addCourse(@RequestBody Course course) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("DEBUG CourseController: Received course data:");
            System.out.println("  Code: " + course.getCourseCode());
            System.out.println("  Name: " + course.getCourseName());
            System.out.println("  DeptId: " + course.getDeptId());
            System.out.println("  FacultyId: " + course.getFacultyId());
            System.out.println("  Credits: " + course.getCredits());
            System.out.println("  MaxCapacity: " + course.getMaxCapacity());
            System.out.println("  Semester: " + course.getSemester());
            System.out.println("  Year: " + course.getYear());
            
            String validationError = courseService.validateCourseData(course);
            System.out.println("DEBUG CourseController: Validation result: " + validationError);
            
            if (!validationError.equals("VALID")) {
                response.put("status", "error");
                response.put("message", validationError);
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            boolean success = courseService.addCourse(course);
            if (success) {
                response.put("status", "success");
                response.put("message", "Course added successfully");
                response.put("data", course);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to add course. Course code may already exist.");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error adding course: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== VIEW COURSES ====================

    /**
     * GET /api/course/all
     * Get all courses in the system
     * 
     * @return List of all courses
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllCourses() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Course> courses = courseService.getAllCourses();

            response.put("status", "success");
            response.put("message", "Courses retrieved successfully");
            response.put("data", courses);
            response.put("count", courses.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving courses: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/course/{courseId}
     * Get specific course by ID
     * 
     * @param courseId Course ID
     * @return Course details
     */
    @GetMapping("/{courseId}")
    public ResponseEntity<Map<String, Object>> getCourseById(@PathVariable("courseId") int courseId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Course course = courseService.getCourseById(courseId);

            if (course != null) {
                response.put("status", "success");
                response.put("message", "Course retrieved successfully");
                response.put("data", course);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Course not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving course: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/course/department/{deptId}
     * Get all courses in a specific department
     * 
     * @param deptId Department ID
     * @return List of courses in this department
     */
    @GetMapping("/department/{deptId}")
    public ResponseEntity<Map<String, Object>> getCoursesByDepartment(@PathVariable("deptId") int deptId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Course> courses = courseService.getCoursesByDepartment(deptId);

            response.put("status", "success");
            response.put("message", "Department courses retrieved successfully");
            response.put("data", courses);
            response.put("count", courses.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving department courses: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/course/faculty/{facultyId}
     * Get all courses taught by specific faculty
     * Faculty use this to see their courses
     * 
     * @param facultyId Faculty ID
     * @return List of courses taught by faculty
     */
    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<Map<String, Object>> getCoursesByFaculty(@PathVariable("facultyId") int facultyId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Course> courses = courseService.getCoursesByFaculty(facultyId);

            response.put("status", "success");
            response.put("message", "Faculty courses retrieved successfully");
            response.put("data", courses);
            response.put("count", courses.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving faculty courses: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/course/code/{courseCode}
     * Get course by course code
     * 
     * @param courseCode Course code (e.g., "CS101")
     * @return Course details
     */
    @GetMapping("/code/{courseCode}")
    public ResponseEntity<Map<String, Object>> getCourseByCode(@PathVariable("courseCode") String courseCode) {
        Map<String, Object> response = new HashMap<>();

        try {
            Course course = courseService.getCourseByCode(courseCode);

            if (course != null) {
                response.put("status", "success");
                response.put("message", "Course retrieved successfully");
                response.put("data", course);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Course not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving course: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== UPDATE COURSE ====================

    /**
     * PUT /api/course/update/{courseId}
     * Admin updates course details
     * 
     * @param courseId Course ID
     * @param course Updated course object
     * @return Success/Error response
     */
    @PutMapping("/update/{courseId}")
    public ResponseEntity<Map<String, Object>> updateCourse(
            @PathVariable("courseId") int courseId,
            @RequestBody Course course) {
        Map<String, Object> response = new HashMap<>();

        try {
            String validationError = courseService.validateCourseData(course);
            if (validationError != null) {
                response.put("status", "error");
                response.put("message", validationError);
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            boolean success = courseService.updateCourse(courseId, course);
            if (success) {
                response.put("status", "success");
                response.put("message", "Course updated successfully");
                response.put("data", course);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to update course");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error updating course: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== DELETE COURSE ====================

    /**
     * DELETE /api/course/delete/{courseId}
     * Admin deletes a course
     * 
     * @param courseId Course ID
     * @return Success/Error response
     */
    @DeleteMapping("/delete/{courseId}")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable("courseId") int courseId) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean success = courseService.deleteCourse(courseId);
            if (success) {
                response.put("status", "success");
                response.put("message", "Course deleted successfully");
                response.put("data", null);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to delete course");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error deleting course: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
