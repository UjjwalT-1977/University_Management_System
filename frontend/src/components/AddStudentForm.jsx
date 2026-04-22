import React, { useState } from "react";
import axios from "axios";

const AddStudentForm = ({ onStudentAdded }) => {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    phone: "",
    address: "",
  });
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");

    try {
      await axios.post("http://localhost:8080/api/admin/addstudent", formData);
      setMessage("Student added successfully!");
      setFormData({
        name: "",
        email: "",
        password: "",
        phone: "",
        address: "",
      });

      if (onStudentAdded) {
        onStudentAdded();
      }

      setTimeout(() => {
        setMessage("");
      }, 3000);
    } catch (err) {
      setError(err.response?.data || "Error adding student.");
    }
  };

  return (
    <section className="ums-section">
      <div className="ums-panel ums-panel--soft">
        <div className="ums-section">
          <p className="ums-eyebrow">Student Administration</p>
          <h2 className="ums-title">Add New Student</h2>
          <p className="ums-subtitle">
            Create a new student account and store their contact details in the
            university system.
          </p>
        </div>

        {message && <div className="ums-alert ums-alert--success">{message}</div>}
        {error && <div className="ums-alert ums-alert--error">{error}</div>}

        <form onSubmit={handleSubmit} className="ums-form">
          <div className="ums-form-grid">
            <div className="ums-field">
              <label className="ums-label" htmlFor="student-name">
                Full Name
              </label>
              <input
                id="student-name"
                className="ums-input"
                type="text"
                name="name"
                placeholder="Enter student name"
                value={formData.name}
                onChange={handleChange}
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="student-email">
                Email Address
              </label>
              <input
                id="student-email"
                className="ums-input"
                type="email"
                name="email"
                placeholder="Enter email address"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="student-password">
                Password
              </label>
              <input
                id="student-password"
                className="ums-input"
                type="password"
                name="password"
                placeholder="Create a temporary password"
                value={formData.password}
                onChange={handleChange}
                required
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="student-phone">
                Phone Number
              </label>
              <input
                id="student-phone"
                className="ums-input"
                type="text"
                name="phone"
                placeholder="Enter phone number"
                value={formData.phone}
                onChange={handleChange}
                required
              />
            </div>

            <div className="ums-field" style={{ gridColumn: "1 / -1" }}>
              <label className="ums-label" htmlFor="student-address">
                Address
              </label>
              <input
                id="student-address"
                className="ums-input"
                type="text"
                name="address"
                placeholder="Enter address"
                value={formData.address}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="ums-section">
            <button type="submit" className="ums-btn ums-btn--primary">
              Add Student
            </button>
          </div>
        </form>
      </div>
    </section>
  );
};

export default AddStudentForm;