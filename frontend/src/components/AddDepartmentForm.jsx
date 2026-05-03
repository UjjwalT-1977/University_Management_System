import React, { useState } from "react";
import axios from "axios";

const AddDepartmentForm = ({ onDepartmentAdded }) => {
  const [departmentData, setDepartmentData] = useState({
    deptName: "",
    deptCode: "",
    hodName: "",
    phone: "",
    email: "",
  });
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleChange = (e) => {
    const { name, value } = e.target;
    setDepartmentData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");

    try {
      await axios.post("http://localhost:8080/api/department/add", departmentData);
      setMessage("Department added successfully!");
      setDepartmentData({
        deptName: "",
        deptCode: "",
        hodName: "",
        phone: "",
        email: "",
      });

      if (onDepartmentAdded) {
        onDepartmentAdded();
      }
    } catch (err) {
      const errorMsg = err.response?.data?.message || err.message || "Failed to add department.";
      setError(typeof errorMsg === 'string' ? errorMsg : "Failed to add department.");
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
          <div className="ums-form-grid">
            <div className="ums-field">
              <label className="ums-label" htmlFor="department-name">
                Department Name
              </label>
              <input
                id="department-name"
                className="ums-input"
                type="text"
                name="deptName"
                placeholder="Enter department name"
                value={departmentData.deptName}
                onChange={handleChange}
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="department-code">
                Department Code
              </label>
              <input
                id="department-code"
                className="ums-input"
                type="text"
                name="deptCode"
                placeholder="Enter department code"
                value={departmentData.deptCode}
                onChange={handleChange}
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="hod-name">
                Head of Department (HOD) Name
              </label>
              <input
                id="hod-name"
                className="ums-input"
                type="text"
                name="hodName"
                placeholder="Enter HOD name"
                value={departmentData.hodName}
                onChange={handleChange}
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="department-phone">
                Phone Number
              </label>
              <input
                id="department-phone"
                className="ums-input"
                type="text"
                name="phone"
                placeholder="Enter phone number"
                value={departmentData.phone}
                onChange={handleChange}
                required
              />
            </div>

            <div className="ums-field" style={{ gridColumn: "1 / -1" }}>
              <label className="ums-label" htmlFor="department-email">
                Email Address
              </label>
              <input
                id="department-email"
                className="ums-input"
                type="email"
                name="email"
                placeholder="Enter email address"
                value={departmentData.email}
                onChange={handleChange}
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