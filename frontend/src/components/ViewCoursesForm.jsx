import React, { useEffect, useMemo, useState } from "react";
import axios from "axios";

const COURSES_API_URL = "http://localhost:8080/api/course/all";
const DEPARTMENTS_API_URL = "http://localhost:8080/api/department/all";
const FACULTY_API_URL = "http://localhost:8080/api/faculty/all";

const priorityOrder = [
  "courseId",
  "id",
  "courseCode",
  "code",
  "courseName",
  "name",
  "departmentId",
  "facultyId",
  "credits",
  "enrolledStudents",
  "currentEnrollment",
  "enrollmentCount",
  "maxStudents",
  "capacity",
  "maxCapacity",
];

const enrollmentKeys = ["enrolledStudents", "currentEnrollment", "enrollmentCount"];
const capacityKeys = ["maxStudents", "capacity", "maxCapacity"];

const getCourseId = (course) => course?.courseId ?? course?.id;

const labelize = (value) =>
  value
    .replace(/([A-Z])/g, " $1")
    .replace(/_/g, " ")
    .replace(/\s+/g, " ")
    .trim()
    .replace(/^./, (char) => char.toUpperCase());

const isScalarValue = (value) =>
  value === null || ["string", "number", "boolean"].includes(typeof value);

const getDisplayValue = (value) => {
  if (value === null || value === undefined || value === "") {
    return "—";
  }

  if (typeof value === "boolean") {
    return value ? "Yes" : "No";
  }

  if (typeof value === "object") {
    return value.name ?? value.courseName ?? value.departmentName ?? value.fullName ?? "—";
  }

  return value;
};

const getOrderedKeys = (items) => {
  if (!items.length) {
    return [];
  }

  const keys = Object.keys(items[0]).filter((key) => isScalarValue(items[0][key]));

  return [
    ...priorityOrder.filter((key) => keys.includes(key)),
    ...keys.filter((key) => !priorityOrder.includes(key)),
  ];
};

const getEnrollmentValue = (course) => {
  const key = enrollmentKeys.find((entry) => entry in course);
  return key ? Number(course[key] ?? 0) : 0;
};

const getCapacityValue = (course) => {
  const key = capacityKeys.find((entry) => entry in course);
  return key ? Number(course[key] ?? 0) : 0;
};

const getEnrollmentBadgeClass = (course) => {
  const enrolled = getEnrollmentValue(course);
  const capacity = getCapacityValue(course);

  if (!capacity) {
    return "ums-badge ums-badge--info";
  }

  if (enrolled >= capacity) {
    return "ums-badge ums-badge--error";
  }

  if (enrolled / capacity >= 0.8) {
    return "ums-badge ums-badge--warning";
  }

  return "ums-badge ums-badge--success";
};

const getEditableKeys = (course) =>
  Object.keys(course).filter(
    (key) =>
      !["courseId", "id", ...enrollmentKeys].includes(key) &&
      isScalarValue(course[key])
  );

const isNumericField = (key) =>
  ["credit", "max", "capacity", "semester", "year", "hour"].some((term) =>
    key.toLowerCase().includes(term)
  );

function ViewCoursesForm({ refreshKey }) {
  const [courses, setCourses] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [facultyList, setFacultyList] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [editedCourse, setEditedCourse] = useState({});
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const fetchData = async () => {
    try {
      const [coursesResponse, departmentsResponse, facultyResponse] = await Promise.all([
        axios.get(COURSES_API_URL),
        axios.get(DEPARTMENTS_API_URL),
        axios.get(FACULTY_API_URL),
      ]);

      setCourses(coursesResponse.data?.data || coursesResponse.data || []);
      setDepartments(departmentsResponse.data?.data || departmentsResponse.data || []);
      setFacultyList(facultyResponse.data?.data || facultyResponse.data || []);
      setError("");
    } catch (err) {
      console.error("Error fetching course data:", err);
      setError("Failed to load courses.");
    }
  };

  useEffect(() => {
    fetchData();
  }, [refreshKey]);

  const departmentMap = useMemo(
    () =>
      departments.reduce((accumulator, department) => {
        const departmentId = department.departmentId ?? department.id;
        accumulator[departmentId] =
          department.departmentName ?? department.name ?? `Department ${departmentId}`;
        return accumulator;
      }, {}),
    [departments]
  );

  const facultyMap = useMemo(
    () =>
      facultyList.reduce((accumulator, faculty) => {
        const facultyId = faculty.facultyId ?? faculty.id;
        accumulator[facultyId] =
          faculty.fullName ??
          faculty.name ??
          ([faculty.firstName, faculty.lastName].filter(Boolean).join(" ") ||
            `Faculty ${facultyId}`);
        return accumulator;
      }, {}),
    [facultyList]
  );

  const handleEditClick = (course) => {
    setEditingId(getCourseId(course));
    setEditedCourse({ ...course });
    setSuccessMessage("");
    setError("");
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setEditedCourse({});
  };

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setEditedCourse((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSaveClick = async () => {
    try {
      const courseId = getCourseId(editedCourse);
      await axios.put(`http://localhost:8080/api/course/update/${courseId}`, editedCourse);
      setEditingId(null);
      setEditedCourse({});
      setSuccessMessage("Course updated successfully.");
      setError("");
      fetchData();
    } catch (err) {
      console.error("Error updating course:", err);
      setError("Failed to update course.");
      setSuccessMessage("");
    }
  };

  const handleDeleteClick = async (courseId) => {
    const confirmDelete = window.confirm(
      "Are you sure you want to delete this course?"
    );

    if (!confirmDelete) {
      return;
    }

    try {
      await axios.delete(`http://localhost:8080/api/course/delete/${courseId}`);
      setCourses((prev) => prev.filter((course) => getCourseId(course) !== courseId));
      if (editingId === courseId) {
        handleCancelEdit();
      }
      setSuccessMessage("Course deleted successfully.");
      setError("");
    } catch (err) {
      console.error("Error deleting course:", err);
      setError("Failed to delete course.");
      setSuccessMessage("");
    }
  };

  const columns = getOrderedKeys(courses);

  return (
    <section className="ums-section">
      <div className="ums-panel ums-panel--soft">
        <div className="ums-section">
          <h2 className="ums-title">Courses</h2>
          <p className="ums-subtitle">
            Review course offerings, adjust assignments, and manage capacity details.
          </p>
          <p className="ums-meta">
            {courses.length} course{courses.length === 1 ? "" : "s"} available
          </p>
        </div>

        {successMessage && (
          <div className="ums-alert ums-alert--success">{successMessage}</div>
        )}

        {error && <div className="ums-alert ums-alert--error">{error}</div>}

        {!courses.length ? (
          <div className="ums-empty-state">No courses available.</div>
        ) : (
          <div className="ums-table-wrap">
            <table className="ums-table">
              <thead>
                <tr>
                  {columns.map((column) => (
                    <th key={column}>{labelize(column)}</th>
                  ))}
                  <th>Enrollment Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {courses.map((course) => {
                  const courseId = getCourseId(course);
                  const isEditing = editingId === courseId;
                  const enrolled = getEnrollmentValue(course);
                  const capacity = getCapacityValue(course);

                  return (
                    <React.Fragment key={courseId}>
                      <tr className={isEditing ? "ums-table__row--editing" : ""}>
                        {columns.map((column) => {
                          let value = getDisplayValue(course[column]);

                          if (column === "departmentId") {
                            value = departmentMap[course[column]] ?? value;
                          }

                          if (column === "facultyId") {
                            value = facultyMap[course[column]] ?? value;
                          }

                          const isBadgeColumn =
                            column === "courseId" ||
                            column === "id" ||
                            column === "departmentId" ||
                            column === "facultyId";

                          return (
                            <td key={column}>
                              {isBadgeColumn ? (
                                <span
                                  className={`ums-badge ${
                                    column === "courseId" || column === "id"
                                      ? "ums-badge--neutral"
                                      : "ums-badge--info"
                                  }`}
                                >
                                  {value}
                                </span>
                              ) : (
                                value
                              )}
                            </td>
                          );
                        })}
                        <td>
                          <span className={getEnrollmentBadgeClass(course)}>
                            {capacity ? `${enrolled}/${capacity} enrolled` : `${enrolled} enrolled`}
                          </span>
                        </td>
                        <td>
                          <div className="ums-table__actions">
                            <button
                              type="button"
                              className="ums-btn ums-btn--primary"
                              onClick={() => handleEditClick(course)}
                            >
                              Edit
                            </button>
                            <button
                              type="button"
                              className="ums-btn ums-btn--danger"
                              onClick={() => handleDeleteClick(courseId)}
                            >
                              Delete
                            </button>
                          </div>
                        </td>
                      </tr>

                      {isEditing && (
                        <tr className="ums-table__row--editing">
                          <td colSpan={columns.length + 2}>
                            <div className="ums-inline-editor">
                              <form
                                className="ums-form"
                                onSubmit={(event) => {
                                  event.preventDefault();
                                  handleSaveClick();
                                }}
                              >
                                <div className="ums-form-grid ums-form-grid--compact">
                                  {getEditableKeys(editedCourse).map((key) => {
                                    if (key === "departmentId") {
                                      return (
                                        <div className="ums-field" key={key}>
                                          <label
                                            className="ums-label"
                                            htmlFor={`course-${courseId}-${key}`}
                                          >
                                            {labelize(key)}
                                          </label>
                                          <select
                                            id={`course-${courseId}-${key}`}
                                            name={key}
                                            value={editedCourse[key] ?? ""}
                                            onChange={handleInputChange}
                                            className="ums-select"
                                          >
                                            <option value="">Select department</option>
                                            {departments.map((department) => {
                                              const departmentId =
                                                department.departmentId ?? department.id;

                                              return (
                                                <option key={departmentId} value={departmentId}>
                                                  {department.departmentName ?? department.name}
                                                </option>
                                              );
                                            })}
                                          </select>
                                        </div>
                                      );
                                    }

                                    if (key === "facultyId") {
                                      return (
                                        <div className="ums-field" key={key}>
                                          <label
                                            className="ums-label"
                                            htmlFor={`course-${courseId}-${key}`}
                                          >
                                            {labelize(key)}
                                          </label>
                                          <select
                                            id={`course-${courseId}-${key}`}
                                            name={key}
                                            value={editedCourse[key] ?? ""}
                                            onChange={handleInputChange}
                                            className="ums-select"
                                          >
                                            <option value="">Select faculty</option>
                                            {facultyList.map((faculty) => {
                                              const facultyId = faculty.facultyId ?? faculty.id;
                                              const facultyName =
                                                faculty.fullName ??
                                                faculty.name ??
                                                [faculty.firstName, faculty.lastName]
                                                  .filter(Boolean)
                                                  .join(" ");

                                              return (
                                                <option key={facultyId} value={facultyId}>
                                                  {facultyName}
                                                </option>
                                              );
                                            })}
                                          </select>
                                        </div>
                                      );
                                    }

                                    return (
                                      <div className="ums-field" key={key}>
                                        <label
                                          className="ums-label"
                                          htmlFor={`course-${courseId}-${key}`}
                                        >
                                          {labelize(key)}
                                        </label>
                                        <input
                                          id={`course-${courseId}-${key}`}
                                          type={isNumericField(key) ? "number" : "text"}
                                          name={key}
                                          value={editedCourse[key] ?? ""}
                                          onChange={handleInputChange}
                                          className="ums-input"
                                        />
                                      </div>
                                    );
                                  })}
                                </div>

                                <div className="ums-table__actions">
                                  <button
                                    type="submit"
                                    className="ums-btn ums-btn--success"
                                  >
                                    Save Changes
                                  </button>
                                  <button
                                    type="button"
                                    className="ums-btn ums-btn--ghost"
                                    onClick={handleCancelEdit}
                                  >
                                    Cancel
                                  </button>
                                </div>
                              </form>
                            </div>
                          </td>
                        </tr>
                      )}
                    </React.Fragment>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </section>
  );
}

export default ViewCoursesForm;