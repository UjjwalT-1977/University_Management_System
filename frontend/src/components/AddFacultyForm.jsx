import React, { useState } from "react";
import axios from "axios";

function AddFacultyForm({ onFacultyAdded }) {
  const [faculty, setFaculty] = useState({
    name: "",
    email: "",
    password: "",
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

    try {
      await axios.post("http://localhost:8080/api/admin/addfaculty", faculty);
      setMessage("Faculty added successfully!");
      setFaculty({ name: "", email: "", password: "" });

      if (onFacultyAdded) {
        onFacultyAdded();
      }
    } catch (err) {
      setError(err.response?.data || "Failed to add faculty.");
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