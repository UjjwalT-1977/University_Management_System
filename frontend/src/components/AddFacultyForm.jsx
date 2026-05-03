import React, { useState } from "react";
import axios from "axios";

function AddFacultyForm({ onFacultyAdded }) {
  const [faculty, setFaculty] = useState({
    name: "",
    email: "",
    phone: "",
    deptId: "",
    password: "",
    empId: "",
    qualification: "",
    specialization: "",
    dateOfJoining: "",
    status: "Active",
  });
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleChange = (e) => {
    setFaculty({ ...faculty, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");

    // Validation
    if (!faculty.name || !faculty.name.trim()) {
      setError("Faculty name is required.");
      return;
    }
    if (!faculty.email || !faculty.email.includes("@")) {
      setError("Valid email is required.");
      return;
    }
    if (!faculty.phone || faculty.phone.length < 10) {
      setError("Phone number must be at least 10 digits.");
      return;
    }
    if (!faculty.deptId) {
      setError("Department ID is required.");
      return;
    }
    if (!faculty.password) {
      setError("Password is required.");
      return;
    }
    if (!faculty.empId) {
      setError("Employee ID is required.");
      return;
    }
    if (!faculty.dateOfJoining) {
      setError("Date of joining is required.");
      return;
    }

    try {
      const payload = {
        ...faculty,
        deptId: parseInt(faculty.deptId, 10),
      };

      await axios.post("http://localhost:8080/api/faculty/add", payload);
      setMessage("Faculty added successfully!");
      setFaculty({ name: "", email: "", phone: "", deptId: "", password: "", empId: "", qualification: "", specialization: "", dateOfJoining: "", status: "Active" });

      if (onFacultyAdded) {
        onFacultyAdded();
      }
    } catch (err) {
      const errorMsg = err.response?.data?.message || err.message || "Failed to add faculty.";
      setError(typeof errorMsg === 'string' ? errorMsg : "Failed to add faculty.");
    }
  };

  return (
    <section className="ums-section">
      <div className="ums-panel ums-panel--soft">
        <div className="ums-section">
          <p className="ums-eyebrow">Faculty Administration</p>
          <h2 className="ums-title">Add Faculty Member</h2>
          <p className="ums-subtitle">
            Register a faculty account so teaching assignments can be managed
            from the admin dashboard.
          </p>
        </div>

        {message && <div className="ums-alert ums-alert--success">{message}</div>}
        {error && <div className="ums-alert ums-alert--error">{error}</div>}

        <form onSubmit={handleSubmit} className="ums-form">
          <div className="ums-form-grid ums-form-grid--compact">
            <div className="ums-field">
              <label className="ums-label" htmlFor="faculty-name">
                Full Name
              </label>
              <input
                id="faculty-name"
                className="ums-input"
                type="text"
                name="name"
                value={faculty.name}
                onChange={handleChange}
                placeholder="Enter faculty name"
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="faculty-email">
                Email Address
              </label>
              <input
                id="faculty-email"
                className="ums-input"
                type="email"
                name="email"
                value={faculty.email}
                onChange={handleChange}
                placeholder="Enter faculty email"
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="faculty-password">
                Password
              </label>
              <input
                id="faculty-password"
                className="ums-input"
                type="password"
                name="password"
                value={faculty.password}
                onChange={handleChange}
                placeholder="Create a temporary password"
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="faculty-phone">
                Phone Number
              </label>
              <input
                id="faculty-phone"
                className="ums-input"
                type="text"
                name="phone"
                value={faculty.phone}
                onChange={handleChange}
                placeholder="Enter phone number"
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="faculty-deptid">
                Department ID
              </label>
              <input
                id="faculty-deptid"
                className="ums-input"
                type="number"
                name="deptId"
                value={faculty.deptId}
                onChange={handleChange}
                placeholder="Enter department ID"
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="faculty-empid">
                Employee ID
              </label>
              <input
                id="faculty-empid"
                className="ums-input"
                type="text"
                name="empId"
                value={faculty.empId}
                onChange={handleChange}
                placeholder="Enter employee ID"
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="faculty-qualification">
                Qualification
              </label>
              <input
                id="faculty-qualification"
                className="ums-input"
                type="text"
                name="qualification"
                value={faculty.qualification}
                onChange={handleChange}
                placeholder="e.g., M.Sc, Ph.D"
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="faculty-specialization">
                Specialization
              </label>
              <input
                id="faculty-specialization"
                className="ums-input"
                type="text"
                name="specialization"
                value={faculty.specialization}
                onChange={handleChange}
                placeholder="e.g., Database Systems"
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="faculty-joining">
                Date of Joining
              </label>
              <input
                id="faculty-joining"
                className="ums-input"
                type="date"
                name="dateOfJoining"
                value={faculty.dateOfJoining}
                onChange={handleChange}
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="faculty-status">
                Status
              </label>
              <select
                id="faculty-status"
                className="ums-input"
                name="status"
                value={faculty.status}
                onChange={handleChange}
              >
                <option value="Active">Active</option>
                <option value="Inactive">Inactive</option>
              </select>
            </div>
          </div>

          <div className="ums-section">
            <button type="submit" className="ums-btn ums-btn--primary">
              Add Faculty
            </button>
          </div>
        </form>
      </div>
    </section>
  );
}

export default AddFacultyForm;