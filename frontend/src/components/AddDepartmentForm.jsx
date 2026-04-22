import React, { useState } from "react";
import axios from "axios";

const AddDepartmentForm = ({ onDepartmentAdded }) => {
  const [departmentName, setDepartmentName] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");

    try {
      await axios.post("http://localhost:8080/api/admin/adddepartment", {
        name: departmentName,
      });
      setMessage("Department added successfully!");
      setDepartmentName("");

      if (onDepartmentAdded) {
        onDepartmentAdded();
      }
    } catch (err) {
      setError(err.response?.data || "Failed to add department.");
    }
  };

  return (
    <section className="ums-section">
      <div className="ums-panel ums-panel--soft">
        <div className="ums-section">
          <p className="ums-eyebrow">Academic Structure</p>
          <h2 className="ums-title">Add Department</h2>
          <p className="ums-subtitle">
            Create a new academic department to organize courses, faculty, and
            students.
          </p>
        </div>

        {message && <div className="ums-alert ums-alert--success">{message}</div>}
        {error && <div className="ums-alert ums-alert--error">{error}</div>}

        <form onSubmit={handleSubmit} className="ums-form">
          <div className="ums-form-grid ums-form-grid--compact">
            <div className="ums-field">
              <label className="ums-label" htmlFor="department-name">
                Department Name
              </label>
              <input
                id="department-name"
                className="ums-input"
                type="text"
                value={departmentName}
                onChange={(e) => setDepartmentName(e.target.value)}
                placeholder="Enter department name"
                required
              />
            </div>
          </div>

          <div className="ums-section">
            <button type="submit" className="ums-btn ums-btn--primary">
              Add Department
            </button>
          </div>
        </form>
      </div>
    </section>
  );
};

export default AddDepartmentForm;