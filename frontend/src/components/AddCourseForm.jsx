import React, { useEffect, useState } from "react";
import axios from "axios";

const AddCourseForm = ({ onCourseAdded }) => {
  const [courseData, setCourseData] = useState({
    courseName: "",
    courseCode: "",
    credits: "",
    departmentId: "",
    facultyId: "",
    maxStudents: "",
  });
  const [departments, setDepartments] = useState([]);
  const [faculties, setFaculties] = useState([]);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchDepartments();
    fetchFaculties();
  }, []);

  const fetchDepartments = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/admin/departments");
      setDepartments(response.data);
    } catch (err) {
      console.error("Error fetching departments:", err);
    }
  };

  const fetchFaculties = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/admin/faculty");
      setFaculties(response.data);
    } catch (err) {
      console.error("Error fetching faculties:", err);
    }
  };

  const handleChange = (e) => {
    setCourseData({ ...courseData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");

    if (
      !courseData.courseName ||
      !courseData.courseCode ||
      !courseData.credits ||
      !courseData.departmentId ||
      !courseData.facultyId ||
      !courseData.maxStudents
    ) {
      setError("Please fill in all fields.");
      return;
    }

    const credits = parseInt(courseData.credits, 10);
    const maxStudents = parseInt(courseData.maxStudents, 10);

    if (Number.isNaN(credits) || credits <= 0) {
      setError("Credits must be a positive number.");
      return;
    }

    if (Number.isNaN(maxStudents) || maxStudents <= 0) {
      setError("Maximum students must be a positive number.");
      return;
    }

    setLoading(true);

    try {
      await axios.post("http://localhost:8080/api/admin/addcourse", {
        ...courseData,
        credits,
        departmentId: parseInt(courseData.departmentId, 10),
        facultyId: parseInt(courseData.facultyId, 10),
        maxStudents,
      });

      setMessage("Course added successfully!");
      setCourseData({
        courseName: "",
        courseCode: "",
        credits: "",
        departmentId: "",
        facultyId: "",
        maxStudents: "",
      });

      if (onCourseAdded) {
        onCourseAdded();
      }
    } catch (err) {
      setError(err.response?.data || "Error adding course.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="ums-section">
      <div className="ums-panel ums-panel--soft">
        <div className="ums-section">
          <p className="ums-eyebrow">Course Management</p>
          <h2 className="ums-title">Add New Course</h2>
          <p className="ums-subtitle">
            Set up a course with its academic department, assigned faculty, and
            enrollment capacity.
          </p>
        </div>

        {message && <div className="ums-alert ums-alert--success">{message}</div>}
        {error && <div className="ums-alert ums-alert--error">{error}</div>}

        <form onSubmit={handleSubmit} className="ums-form">
          <div className="ums-form-grid">
            <div className="ums-field">
              <label className="ums-label" htmlFor="course-name">
                Course Name
              </label>
              <input
                id="course-name"
                className="ums-input"
                type="text"
                name="courseName"
                value={courseData.courseName}
                onChange={handleChange}
                placeholder="Enter course title"
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="course-code">
                Course Code
              </label>
              <input
                id="course-code"
                className="ums-input"
                type="text"
                name="courseCode"
                value={courseData.courseCode}
                onChange={handleChange}
                placeholder="e.g. CS101"
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="course-credits">
                Credits
              </label>
              <input
                id="course-credits"
                className="ums-input"
                type="number"
                name="credits"
                value={courseData.credits}
                onChange={handleChange}
                placeholder="Enter credit value"
                min="1"
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="course-capacity">
                Maximum Students
              </label>
              <input
                id="course-capacity"
                className="ums-input"
                type="number"
                name="maxStudents"
                value={courseData.maxStudents}
                onChange={handleChange}
                placeholder="Set enrollment capacity"
                min="1"
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="course-department">
                Department
              </label>
              <select
                id="course-department"
                className="ums-select"
                name="departmentId"
                value={courseData.departmentId}
                onChange={handleChange}
                required
              >
                <option value="">Select Department</option>
                {departments.map((dept) => (
                  <option key={dept.id} value={dept.id}>
                    {dept.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="course-faculty">
                Faculty
              </label>
              <select
                id="course-faculty"
                className="ums-select"
                name="facultyId"
                value={courseData.facultyId}
                onChange={handleChange}
                required
              >
                <option value="">Select Faculty</option>
                {faculties.map((faculty) => (
                  <option key={faculty.id} value={faculty.id}>
                    {faculty.name}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="ums-section">
            <button
              type="submit"
              className={`ums-btn ums-btn--primary ${loading ? "is-disabled" : ""}`.trim()}
              disabled={loading}
            >
              {loading ? "Adding Course..." : "Add Course"}
            </button>
          </div>
        </form>
      </div>
    </section>
  );
};

export default AddCourseForm;