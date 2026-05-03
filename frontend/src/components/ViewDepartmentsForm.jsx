import React, { useEffect, useState } from "react";
import axios from "axios";

const API_URL = "http://localhost:8080/api/department/all";

const priorityOrder = [
  "departmentId",
  "id",
  "departmentName",
  "name",
  "departmentCode",
  "code",
  "headOfDepartment",
  "description",
];

const getDepartmentId = (department) => department?.departmentId ?? department?.id;

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
    return value.name ?? value.departmentName ?? "—";
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
    (key) => !["departmentId", "id"].includes(key) && isScalarValue(item[key])
  );

function ViewDepartmentsForm({ refreshKey }) {
  const [departments, setDepartments] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [editedDepartment, setEditedDepartment] = useState({});
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const fetchDepartments = async () => {
    try {
      const response = await axios.get(API_URL);
      setDepartments(response.data?.data || response.data || []);
      setError("");
    } catch (err) {
      console.error("Error fetching departments:", err);
      setError("Failed to load departments.");
    }
  };

  useEffect(() => {
    fetchDepartments();
  }, [refreshKey]);

  const handleEditClick = (department) => {
    setEditingId(getDepartmentId(department));
    setEditedDepartment({ ...department });
    setSuccessMessage("");
    setError("");
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setEditedDepartment({});
  };

  const handleChange = (event) => {
    const { name, value } = event.target;
    setEditedDepartment((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSaveClick = async () => {
    try {
      const departmentId = getDepartmentId(editedDepartment);
      await axios.put(`http://localhost:8080/api/department/update/${departmentId}`, editedDepartment);
      setEditingId(null);
      setEditedDepartment({});
      setSuccessMessage("Department updated successfully.");
      setError("");
      fetchDepartments();
    } catch (err) {
      console.error("Error updating department:", err);
      setError("Failed to update department.");
      setSuccessMessage("");
    }
  };

  const handleDeleteClick = async (departmentId) => {
    const confirmDelete = window.confirm(
      "Are you sure you want to delete this department?"
    );

    if (!confirmDelete) {
      return;
    }

    try {
      await axios.delete(`http://localhost:8080/api/department/delete/${departmentId}`);
      setDepartments((prev) =>
        prev.filter((department) => getDepartmentId(department) !== departmentId)
      );
      if (editingId === departmentId) {
        handleCancelEdit();
      }
      setSuccessMessage("Department deleted successfully.");
      setError("");
    } catch (err) {
      console.error("Error deleting department:", err);
      setError("Failed to delete department.");
      setSuccessMessage("");
    }
  };

  const columns = getOrderedKeys(departments);

  return (
    <section className="ums-section">
      <div className="ums-panel ums-panel--soft">
        <div className="ums-section">
          <h2 className="ums-title">Departments</h2>
          <p className="ums-subtitle">
            Edit department information and remove records that are no longer needed.
          </p>
          <p className="ums-meta">
            {departments.length} department{departments.length === 1 ? "" : "s"} available
          </p>
        </div>

        {successMessage && (
          <div className="ums-alert ums-alert--success">{successMessage}</div>
        )}

        {error && <div className="ums-alert ums-alert--error">{error}</div>}

        {!departments.length ? (
          <div className="ums-empty-state">No departments available.</div>
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
                {departments.map((department) => {
                  const departmentId = getDepartmentId(department);
                  const isEditing = editingId === departmentId;

                  return (
                    <React.Fragment key={departmentId}>
                      <tr className={isEditing ? "ums-table__row--editing" : ""}>
                        {columns.map((column) => (
                          <td key={column}>
                            {column === "departmentId" || column === "id" ? (
                              <span className="ums-badge ums-badge--neutral">
                                {getDisplayValue(department[column])}
                              </span>
                            ) : column.toLowerCase().includes("code") ? (
                              <span className="ums-badge ums-badge--info">
                                {getDisplayValue(department[column])}
                              </span>
                            ) : (
                              getDisplayValue(department[column])
                            )}
                          </td>
                        ))}
                        <td>
                          <div className="ums-table__actions">
                            <button
                              type="button"
                              className="ums-btn ums-btn--primary"
                              onClick={() => handleEditClick(department)}
                            >
                              Edit
                            </button>
                            <button
                              type="button"
                              className="ums-btn ums-btn--danger"
                              onClick={() => handleDeleteClick(departmentId)}
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
                                  {getEditableKeys(editedDepartment).map((key) => (
                                    <div className="ums-field" key={key}>
                                      <label
                                        className="ums-label"
                                        htmlFor={`department-${departmentId}-${key}`}
                                      >
                                        {labelize(key)}
                                      </label>
                                      <input
                                        id={`department-${departmentId}-${key}`}
                                        type="text"
                                        name={key}
                                        value={editedDepartment[key] ?? ""}
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

export default ViewDepartmentsForm;