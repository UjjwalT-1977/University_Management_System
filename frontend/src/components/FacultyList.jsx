import React, { useEffect, useState } from "react";
import axios from "axios";

const API_URL = "http://localhost:8080/api/faculty/all";

const priorityOrder = [
  "facultyId",
  "id",
  "firstName",
  "lastName",
  "name",
  "fullName",
  "email",
  "phone",
  "departmentId",
  "departmentName",
  "designation",
  "status",
];

const getFacultyId = (faculty) => faculty?.facultyId ?? faculty?.id;

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
    (key) => !["facultyId", "id"].includes(key) && isScalarValue(item[key])
  );

const getBadgeClassName = (key, value) => {
  const normalizedValue = String(value ?? "").toLowerCase();

  if (key === "facultyId" || key === "id") {
    return "ums-badge ums-badge--neutral";
  }

  if (key.toLowerCase().includes("department")) {
    return "ums-badge ums-badge--info";
  }

  if (normalizedValue === "active") {
    return "ums-badge ums-badge--success";
  }

  if (normalizedValue === "inactive") {
    return "ums-badge ums-badge--warning";
  }

  return "ums-badge ums-badge--neutral";
};

function FacultyList({ refreshKey }) {
  const [facultyList, setFacultyList] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [editedFaculty, setEditedFaculty] = useState({});
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const fetchFaculty = async () => {
    try {
      const response = await axios.get(API_URL);
      setFacultyList(response.data?.data || response.data || []);
      setError("");
    } catch (err) {
      console.error("Error fetching faculty:", err);
      setError("Failed to load faculty.");
    }
  };

  useEffect(() => {
    fetchFaculty();
  }, [refreshKey]);

  const handleEditClick = (faculty) => {
    setEditingId(getFacultyId(faculty));
    setEditedFaculty({ ...faculty });
    setSuccessMessage("");
    setError("");
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setEditedFaculty({});
  };

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setEditedFaculty((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSaveClick = async () => {
    try {
      const facultyId = getFacultyId(editedFaculty);
      await axios.put(`http://localhost:8080/api/faculty/update/${facultyId}`, editedFaculty);
      setEditingId(null);
      setEditedFaculty({});
      setSuccessMessage("Faculty record updated successfully.");
      setError("");
      fetchFaculty();
    } catch (err) {
      console.error("Error updating faculty:", err);
      setError("Failed to update faculty.");
      setSuccessMessage("");
    }
  };

  const handleDeleteClick = async (facultyId) => {
    const confirmDelete = window.confirm(
      "Are you sure you want to delete this faculty record?"
    );

    if (!confirmDelete) {
      return;
    }

    try {
      await axios.delete(`http://localhost:8080/api/faculty/delete/${facultyId}`);
      setFacultyList((prev) =>
        prev.filter((faculty) => getFacultyId(faculty) !== facultyId)
      );
      if (editingId === facultyId) {
        handleCancelEdit();
      }
      setSuccessMessage("Faculty record deleted successfully.");
      setError("");
    } catch (err) {
      console.error("Error deleting faculty:", err);
      setError("Failed to delete faculty.");
      setSuccessMessage("");
    }
  };

  const columns = getOrderedKeys(facultyList);

  return (
    <section className="ums-section">
      <div className="ums-panel ums-panel--soft">
        <div className="ums-section">
          <h2 className="ums-title">Faculty Directory</h2>
          <p className="ums-subtitle">
            Manage faculty records, update details, and remove outdated entries.
          </p>
          <p className="ums-meta">
            {facultyList.length} faculty member{facultyList.length === 1 ? "" : "s"} listed
          </p>
        </div>

        {successMessage && (
          <div className="ums-alert ums-alert--success">{successMessage}</div>
        )}

        {error && <div className="ums-alert ums-alert--error">{error}</div>}

        {!facultyList.length ? (
          <div className="ums-empty-state">No faculty records available.</div>
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
                {facultyList.map((faculty) => {
                  const facultyId = getFacultyId(faculty);
                  const isEditing = editingId === facultyId;

                  return (
                    <React.Fragment key={facultyId}>
                      <tr className={isEditing ? "ums-table__row--editing" : ""}>
                        {columns.map((column) => {
                          const value = getDisplayValue(faculty[column]);
                          const shouldBadge =
                            column === "facultyId" ||
                            column === "id" ||
                            column.toLowerCase().includes("department") ||
                            column.toLowerCase().includes("status");

                          return (
                            <td key={column}>
                              {shouldBadge ? (
                                <span className={getBadgeClassName(column, value)}>
                                  {value}
                                </span>
                              ) : (
                                value
                              )}
                            </td>
                          );
                        })}
                        <td>
                          <div className="ums-table__actions">
                            <button
                              type="button"
                              className="ums-btn ums-btn--primary"
                              onClick={() => handleEditClick(faculty)}
                            >
                              Edit
                            </button>
                            <button
                              type="button"
                              className="ums-btn ums-btn--danger"
                              onClick={() => handleDeleteClick(facultyId)}
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
                                  {getEditableKeys(editedFaculty).map((key) => (
                                    <div className="ums-field" key={key}>
                                      <label
                                        className="ums-label"
                                        htmlFor={`faculty-${facultyId}-${key}`}
                                      >
                                        {labelize(key)}
                                      </label>
                                      <input
                                        id={`faculty-${facultyId}-${key}`}
                                        type="text"
                                        name={key}
                                        value={editedFaculty[key] ?? ""}
                                        onChange={handleInputChange}
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

export default FacultyList;