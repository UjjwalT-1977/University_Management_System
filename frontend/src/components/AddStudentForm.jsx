import React, { useState } from "react";
import axios from "axios";

const AddStudentForm = ({ onStudentAdded }) => {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    phone: "",
    deptId: "",
    rollNumber: "",
    dateOfBirth: "",
    gender: "",
    admissionDate: "",
    semester: "1",
    status: "Active",
    cgpa: "0.0",
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

    // Validation (Keep your existing validation logic here)
    if (!formData.name || !formData.name.trim()) {
      setError("Student name is required.");
      return;
    }
    if (!formData.email || !formData.email.includes("@")) {
      setError("Valid email is required.");
      return;
    }
    if (!formData.phone || formData.phone.length < 10) {
      setError("Phone number must be at least 10 digits.");
      return;
    }
    if (!formData.password) {
      setError("Password is required.");
      return;
    }
    if (!formData.deptId) {
      setError("Department ID is required.");
      return;
    }
    if (!formData.rollNumber) {
      setError("Roll number is required.");
      return;
    }
    if (!formData.dateOfBirth) {
      setError("Date of birth is required.");
      return;
    }
    if (!formData.gender) {
      setError("Gender is required.");
      return;
    }
    if (!formData.admissionDate) {
      setError("Admission date is required.");
      return;
    }

    const datePattern = /^\d{4}-\d{2}-\d{2}$/;
    if (!datePattern.test(formData.dateOfBirth)) {
      setError("Date of birth must use YYYY-MM-DD format.");
      return;
    }
    if (!datePattern.test(formData.admissionDate)) {
      setError("Admission date must use YYYY-MM-DD format.");
      return;
    }

    // NEW LOGIC: Format the data types properly before sending
    const payload = {
      ...formData,
      deptId: parseInt(formData.deptId, 10),
      semester: parseInt(formData.semester, 10),
      cgpa: parseFloat(formData.cgpa),
      dateOfBirth: formData.dateOfBirth,
      admissionDate: formData.admissionDate,
    };

    try {
      await axios.post("http://localhost:8080/api/student/add", payload);
      setMessage("Student added successfully!");
      
      // Reset form
      setFormData({
        name: "",
        email: "",
        password: "",
        phone: "",
        deptId: "",
        rollNumber: "",
        dateOfBirth: "",
        gender: "",
        admissionDate: "",
        semester: "1",
        status: "Active",
        cgpa: "0.0",
      });

      if (onStudentAdded) {
        onStudentAdded();
      }

      setTimeout(() => {
        setMessage("");
      }, 3000);
    } catch (err) {
      const errorMsg = err.response?.data?.message || err.message || "Error adding student.";
      setError(typeof errorMsg === 'string' ? errorMsg : "Error adding student.");
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

            <div className="ums-field">
              <label className="ums-label" htmlFor="student-deptid">
                Department ID
              </label>
              <input
                id="student-deptid"
                className="ums-input"
                type="number"
                name="deptId"
                placeholder="Enter department ID"
                value={formData.deptId}
                onChange={handleChange}
                required
              />
            </div>

            <div className="ums-field" style={{ gridColumn: "1 / -1" }}>
              <label className="ums-label" htmlFor="student-rollnumber">
                Roll Number
              </label>
              <input
                id="student-rollnumber"
                className="ums-input"
                type="text"
                name="rollNumber"
                placeholder="Enter roll number"
                value={formData.rollNumber || ""}
                onChange={handleChange}
              />
            </div>

            <div className="ums-field" style={{ gridColumn: "1 / -1" }}>
              <label className="ums-label" htmlFor="student-dob">
                Date of Birth
              </label>
              <input
                id="student-dob"
                className="ums-input"
                type="text"
                inputMode="numeric"
                pattern="\d{4}-\d{2}-\d{2}"
                placeholder="YYYY-MM-DD"
                name="dateOfBirth"
                value={formData.dateOfBirth || ""}
                onChange={handleChange}
                required
                maxLength={10}
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="student-gender">
                Gender
              </label>
              <select
                id="student-gender"
                className="ums-input"
                name="gender"
                value={formData.gender}
                onChange={handleChange}
              >
                <option value="">Select Gender</option>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
              </select>
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="student-admission">
                Admission Date
              </label>
              <input
                id="student-admission"
                className="ums-input"
                type="text"
                inputMode="numeric"
                pattern="\d{4}-\d{2}-\d{2}"
                placeholder="YYYY-MM-DD"
                name="admissionDate"
                value={formData.admissionDate || ""}
                onChange={handleChange}
                required
                maxLength={10}
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="student-semester">
                Semester
              </label>
              <input
                id="student-semester"
                className="ums-input"
                type="number"
                name="semester"
                placeholder="Enter semester (1-8)"
                value={formData.semester}
                onChange={handleChange}
                min="1"
                max="8"
              />
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="student-status">
                Status
              </label>
              <select
                id="student-status"
                className="ums-input"
                name="status"
                value={formData.status}
                onChange={handleChange}
              >
                <option value="Active">Active</option>
                <option value="Inactive">Inactive</option>
              </select>
            </div>

            <div className="ums-field">
              <label className="ums-label" htmlFor="student-cgpa">
                CGPA
              </label>
              <input
                id="student-cgpa"
                className="ums-input"
                type="number"
                name="cgpa"
                placeholder="Enter CGPA (0.0 - 4.0)"
                value={formData.cgpa}
                onChange={handleChange}
                step="0.01"
                min="0"
                max="4"
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