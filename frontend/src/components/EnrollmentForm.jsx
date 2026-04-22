import React, { useEffect, useMemo, useState } from "react";
import axios from "axios";

const EnrollmentForm = ({ adminId, onEnrollmentSuccess }) => {
  const [students, setStudents] = useState([]);
  const [courses, setCourses] = useState([]);
  const [selectedStudent, setSelectedStudent] = useState("");
  const [selectedCourse, setSelectedCourse] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [enrollmentStatus, setEnrollmentStatus] = useState(false);

  useEffect(() => {
    fetchStudents();
    fetchCourses();
  }, []);

  useEffect(() => {
    if (selectedStudent && selectedCourse) {
      checkEnrollmentStatus(selectedStudent, selectedCourse);
    } else {
      setEnrollmentStatus(false);
      setError("");
    }
  }, [selectedStudent, selectedCourse]);

  const fetchStudents = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/admin/students");
      setStudents(response.data);
    } catch (err) {
      console.error("Error fetching students:", err);
    }
  };

  const fetchCourses = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/admin/courses");
      setCourses(response.data);
    } catch (err) {
      console.error("Error fetching courses:", err);
    }
  };

  const checkEnrollmentStatus = async (studentId, courseId) => {
    try {
      const response = await axios.get(
        `http://localhost:8080/api/admin/check-enrollment?studentId=${studentId}&courseId=${courseId}`
      );

      setEnrollmentStatus(response.data);

      if (response.data) {
        setError("Student is already enrolled in this course.");
      } else {
        setError("");
      }
    } catch (err) {
      console.error("Error checking enrollment status:", err);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");

    if (!selectedStudent || !selectedCourse) {
      setError("Please select both student and course.");
      return;
    }

    if (enrollmentStatus) {
      setError("Student is already enrolled in this course.");
      return;
    }

    setLoading(true);

    try {
      await axios.post("http://localhost:8080/api/admin/enroll", {
        studentId: parseInt(selectedStudent, 10),
        courseId: parseInt(selectedCourse, 10),
        adminId,
      });

      setMessage("Student enrolled successfully!");
      setSelectedStudent("");
      setSelectedCourse("");
      setEnrollmentStatus(false);

      if (onEnrollmentSuccess) {
        onEnrollmentSuccess();
      }
    } catch (err) {
      setError(err.response?.data || "Error enrolling student.");
    } finally {
      setLoading(false);
    }
  };

  const selectedCourseDetails = useMemo(() => {
    return (
      courses.find((course) => String(course.id) === String(selectedCourse)) || null
    );
  }, [courses, selectedCourse]);

  const enrolledCount = selectedCourseDetails
    ? Number(
        selectedCourseDetails.enrolledStudentsCount ??
          selectedCourseDetails.currentEnrollment ??
          selectedCourseDetails.currentStudents ??
          selectedCourseDetails.enrolledStudents ??
          selectedCourseDetails.enrollmentCount ??
          0
      )
    : 0;

  const maxStudents = selectedCourseDetails
    ? Number(selectedCourseDetails.maxStudents ?? 0)
    : 0;

  const availableSeats = Math.max(maxStudents - enrolledCount, 0);
  const isCourseFull = selectedCourseDetails ? availableSeats <= 0 : false;

  const capacityBadgeClass = isCourseFull
    ? "ums-badge ums-badge--error"
    : availableSeats <= 5
    ? "ums-badge ums-badge--warning"
    : "ums-badge ums-badge--success";

  const isSubmitDisabled =
    loading || enrollmentStatus || isCourseFull || !selectedStudent || !selectedCourse;

  return (
    <section className="ums-section">
      <div className="ums-panel ums-panel--soft">
        <div className="ums-section">
          <p className="ums-eyebrow">Enrollment Management</p>
          <h2 className="ums-title">Enroll Student in Course</h2>
          <p className="ums-subtitle">
            Match students to available courses while checking duplicate
            enrollments and current seat capacity.
          </p>
        </div>

        {message && <div className="ums-alert ums-alert--success">{message}</div>}
        {error && <div className="ums-alert ums-alert--error">{error}</div>}

        <form onSubmit={handleSubmit} className="ums-form">
          <div className="ums-form-grid">
            <div className="ums-field">
              <label className="ums-label" htmlFor="enrollment-student">
                Student
              </label>
              <select
                id="enrollment-student"
                className="ums-select"
                value={selectedStudent}
                onChange={(e) => setSelectedStudent(e.target.value)}
                required
              >
                <option value="">Select Student</option>
                {students.map((student) => (
                  <option key={student.id} value={student.id}>
                    {student.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="enrollment-course">
                Course
              </label>
              <select
                id="enrollment-course"
                className="ums-select"
                value={selectedCourse}
                onChange={(e) => setSelectedCourse(e.target.value)}
                required
              >
                <option value="">Select Course</option>
                {courses.map((course) => (
                  <option key={course.id} value={course.id}>
                    {course.courseName} ({course.courseCode})
                  </option>
                ))}
              </select>
            </div>
          </div>

          {selectedCourseDetails ? (
            <div className="ums-panel ums-panel--accent">
              <div className="ums-section">
                <h3 className="ums-title">{selectedCourseDetails.courseName}</h3>
                <p className="ums-meta">
                  {selectedCourseDetails.courseCode}
                  {selectedCourseDetails.credits
                    ? ` • ${selectedCourseDetails.credits} Credits`
                    : ""}
                </p>
              </div>

              <div className="ums-grid ums-grid--cards">
                <div className="ums-stat-card">
                  <p className="ums-muted">Capacity</p>
                  <p className="ums-title">{maxStudents}</p>
                </div>
                <div className="ums-stat-card">
                  <p className="ums-muted">Currently Enrolled</p>
                  <p className="ums-title">{enrolledCount}</p>
                </div>
                <div className="ums-stat-card">
                  <p className="ums-muted">Seats Remaining</p>
                  <p className="ums-title">{availableSeats}</p>
                </div>
              </div>

              <div className="ums-section">
                <span className={capacityBadgeClass}>
                  {isCourseFull
                    ? "Course Full"
                    : availableSeats <= 5
                    ? "Limited Seats"
                    : "Seats Available"}
                </span>
              </div>
            </div>
          ) : (
            <div className="ums-empty-state">
              Select a course to view its capacity and enrollment summary.
            </div>
          )}

          <div className="ums-section">
            <button
              type="submit"
              className={`ums-btn ums-btn--primary ${isSubmitDisabled ? "is-disabled" : ""}`.trim()}
              disabled={isSubmitDisabled}
            >
              {loading ? "Enrolling..." : "Enroll Student"}
            </button>
          </div>
        </form>
      </div>
    </section>
  );
};

export default EnrollmentForm;