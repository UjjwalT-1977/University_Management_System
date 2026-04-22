import React, { useEffect, useState } from "react";
import axios from "axios";

const API_URL = "http://localhost:8080/api/students";

const priorityOrder = [
  "studentId",
  "id",
  "firstName",
  "lastName",
  "name",
  "fullName",
  "email",
  "phone",
  "departmentId",
  "departmentName",
  "semester",
  "status",
];

const getStudentId = (student) => student?.studentId ?? student?.id;

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
    return value.name ?? value.fullName ?? value.departmentName ?? "—";
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

const getEditableKeys = (item) =>
  Object.keys(item).filter(
    (key) => !["studentId", "id"].includes(key) && isScalarValue(item[key])
  );

function StudentList({ refreshKey }) {
  const [students, setStudents] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [editedStudent, setEditedStudent] = useState({});
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const fetchStudents = async () => {
    try {
      const response = await axios.get(API_URL);
      setStudents(response.data);
      setError("");
    } catch (err) {
      console.error("Error fetching students:", err);
      setError("Failed to load students.");
    }
  };

  useEffect(() => {
    fetchStudents();
  }, [refreshKey]);

  const handleEditClick = (student) => {
    setEditingId(getStudentId(student));
    setEditedStudent({ ...student });
    setSuccessMessage("");
    setError("");
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setEditedStudent({});
  };

  const handleChange = (event) => {
    const { name, value } = event.target;
    setEditedStudent((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSaveClick = async () => {
    try {
      const studentId = getStudentId(editedStudent);
      await axios.put(`${API_URL}/${studentId}`, editedStudent);
      setEditingId(null);
      setEditedStudent({});
      setSuccessMessage("Student updated successfully.");
      setError("");
      fetchStudents();
    } catch (err) {
      console.error("Error updating student:", err);
      setError("Failed to update student.");
      setSuccessMessage("");
    }
  };

  const handleDeleteClick = async (studentId) => {
    const confirmDelete = window.confirm(
      "Are you sure you want to delete this student?"
    );

    if (!confirmDelete) {
      return;
    }

    try {
      await axios.delete(`${API_URL}/${studentId}`);
      setStudents((prev) =>
        prev.filter((student) => getStudentId(student) !== studentId)
      );
      if (editingId === studentId) {
        handleCancelEdit();
      }
      setSuccessMessage("Student deleted successfully.");
      setError("");
    } catch (err) {
      console.error("Error deleting student:", err);
      setError("Failed to delete student.");
      setSuccessMessage("");
    }
  };

  const columns = getOrderedKeys(students);

  return (
    <section className="ums-section">
      <div className="ums-panel ums-panel--soft">
        <div className="ums-section">
          <h2 className="ums-title">Students Directory</h2>
          <p className="ums-subtitle">
            Review student records, edit details, and remove entries when needed.
          </p>
          <p className="ums-meta">
            {students.length} student{students.length === 1 ? "" : "s"} found
          </p>
        </div>

        {successMessage && (
          <div className="ums-alert ums-alert--success">{successMessage}</div>
        )}

        {error && <div className="ums-alert ums-alert--error">{error}</div>}

        {!students.length ? (
          <div className="ums-empty-state">No students available.</div>
        ) : (
          <div className="ums-table-wrap">
            <table className="ums-table">
              <thead>
                <tr>
                  {columns.map((column) => (
                    <th key={column}>{labelize(column)}</th>
                  ))}
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {students.map((student) => {
                  const studentId = getStudentId(student);
                  const isEditing = editingId === studentId;

                  return (
                    <React.Fragment key={studentId}>
                      <tr className={isEditing ? "ums-table__row--editing" : ""}>
                        {columns.map((column) => (
                          <td key={column}>
                            {column === "studentId" || column === "id" ? (
                              <span className="ums-badge ums-badge--neutral">
                                {getDisplayValue(student[column])}
                              </span>
                            ) : (
                              getDisplayValue(student[column])
                            )}
                          </td>
                        ))}
                        <td>
                          <div className="ums-table__actions">
                            <button
                              type="button"
                              className="ums-btn ums-btn--primary"
                              onClick={() => handleEditClick(student)}
                            >
                              Edit
                            </button>
                            <button
                              type="button"
                              className="ums-btn ums-btn--danger"
                              onClick={() => handleDeleteClick(studentId)}
                            >
                              Delete
                            </button>
                          </div>
                        </td>
                      </tr>

                      {isEditing && (
                        <tr className="ums-table__row--editing">
                          <td colSpan={columns.length + 1}>
                            <div className="ums-inline-editor">
                              <form
                                className="ums-form"
                                onSubmit={(event) => {
                                  event.preventDefault();
                                  handleSaveClick();
                                }}
                              >
                                <div className="ums-form-grid ums-form-grid--compact">
                                  {getEditableKeys(editedStudent).map((key) => (
                                    <div className="ums-field" key={key}>
                                      <label
                                        className="ums-label"
                                        htmlFor={`student-${studentId}-${key}`}
                                      >
                                        {labelize(key)}
                                      </label>
                                      <input
                                        id={`student-${studentId}-${key}`}
                                        type="text"
                                        name={key}
                                        value={editedStudent[key] ?? ""}
                                        onChange={handleChange}
                                        className="ums-input"
                                      />
                                    </div>
                                  ))}
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

export default StudentList;