import { useState, useEffect } from 'react';
import axios from 'axios';

export default function StudentList() {
    const [students, setStudents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [message, setMessage] = useState('');
    const [messageType, setMessageType] = useState('');
    const [editingId, setEditingId] = useState(null);
    const [editData, setEditData] = useState(null);

    useEffect(() => {
        fetchStudents();
    }, []);

    const fetchStudents = async () => {
        setLoading(true);
        try {
            const response = await axios.get('http://localhost:8080/api/student/all');
            if (response.data.status === 'success') {
                setStudents(response.data.data || []);
            }
        } catch (error) {
            console.error('❌ Fetch Students Error:', error);
            console.error('Error Details:', error.response?.data || error.message);
            setMessageType('error');
            setMessage('Failed to load students: ' + (error.message || 'Unknown error'));
        } finally {
            setLoading(false);
        }
    };

    const handleEdit = (student) => {
        setEditingId(student.studentId);
        setEditData({ ...student });
    };

    const handleSaveEdit = async () => {
        try {
            const response = await axios.put(
                `http://localhost:8080/api/student/update/${editingId}`,
                editData
            );
            
            if (response.data.status === 'success') {
                setMessageType('success');
                setMessage('✓ Student updated successfully!');
                setEditingId(null);
                fetchStudents();
                setTimeout(() => setMessage(''), 3000);
            }
        } catch (error) {
            setMessageType('error');
            setMessage('✗ Failed to update student');
        }
    };

    const handleDelete = async (studentId) => {
        if (window.confirm('Are you sure you want to delete this student?')) {
            try {
                const response = await axios.delete(`http://localhost:8080/api/student/delete/${studentId}`);
                
                if (response.data.status === 'success') {
                    setMessageType('success');
                    setMessage('✓ Student deleted successfully!');
                    fetchStudents();
                    setTimeout(() => setMessage(''), 3000);
                }
            } catch (error) {
                setMessageType('error');
                setMessage('✗ Failed to delete student');
            }
        }
    };

    if (loading) {
        return <div style={{ textAlign: 'center', padding: '40px' }}>Loading students...</div>;
    }

    return (
        <div style={{ padding: '20px' }}>
            <h2>Students Management</h2>

            {message && (
                <div style={{
                    padding: '12px',
                    backgroundColor: messageType === 'success' ? '#d4edda' : '#f8d7da',
                    color: messageType === 'success' ? '#155724' : '#721c24',
                    borderRadius: '4px',
                    marginBottom: '20px'
                }}>
                    {message}
                </div>
            )}

            {students.length === 0 ? (
                <p>No students found. <a href="#" onClick={() => window.location.reload()}>Refresh</a></p>
            ) : (
                <div style={{ overflowX: 'auto' }}>
                    <table style={{
                        width: '100%',
                        borderCollapse: 'collapse',
                        border: '1px solid #ddd'
                    }}>
                        <thead>
                            <tr style={{ backgroundColor: '#f8f9fa' }}>
                                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>ID</th>
                                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Roll Number</th>
                                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Name</th>
                                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Email</th>
                                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Semester</th>
                                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Status</th>
                                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {students.map(student => (
                                editingId === student.studentId ? (
                                    <tr key={student.studentId} style={{ backgroundColor: '#fff3cd' }}>
                                        <td colSpan="7" style={{ padding: '15px' }}>
                                            <h4>Edit Student</h4>
                                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', marginBottom: '10px' }}>
                                                <div>
                                                    <label>Name:</label>
                                                    <input
                                                        type="text"
                                                        value={editData.name}
                                                        onChange={(e) => setEditData({ ...editData, name: e.target.value })}
                                                        style={{ width: '100%', padding: '5px', borderRadius: '3px', border: '1px solid #ddd' }}
                                                    />
                                                </div>
                                                <div>
                                                    <label>Email:</label>
                                                    <input
                                                        type="email"
                                                        value={editData.email}
                                                        onChange={(e) => setEditData({ ...editData, email: e.target.value })}
                                                        style={{ width: '100%', padding: '5px', borderRadius: '3px', border: '1px solid #ddd' }}
                                                    />
                                                </div>
                                                <div>
                                                    <label>Phone:</label>
                                                    <input
                                                        type="text"
                                                        value={editData.phone}
                                                        onChange={(e) => setEditData({ ...editData, phone: e.target.value })}
                                                        style={{ width: '100%', padding: '5px', borderRadius: '3px', border: '1px solid #ddd' }}
                                                    />
                                                </div>
                                                <div>
                                                    <label>Semester:</label>
                                                    <input
                                                        type="number"
                                                        value={editData.semester}
                                                        onChange={(e) => setEditData({ ...editData, semester: parseInt(e.target.value) })}
                                                        style={{ width: '100%', padding: '5px', borderRadius: '3px', border: '1px solid #ddd' }}
                                                    />
                                                </div>
                                                <div>
                                                    <label>Status:</label>
                                                    <select
                                                        value={editData.status}
                                                        onChange={(e) => setEditData({ ...editData, status: e.target.value })}
                                                        style={{ width: '100%', padding: '5px', borderRadius: '3px', border: '1px solid #ddd' }}
                                                    >
                                                        <option>Active</option>
                                                        <option>Inactive</option>
                                                        <option>Graduated</option>
                                                    </select>
                                                </div>
                                            </div>
                                            <div>
                                                <button
                                                    onClick={handleSaveEdit}
                                                    style={{
                                                        padding: '8px 15px',
                                                        backgroundColor: '#28a745',
                                                        color: 'white',
                                                        border: 'none',
                                                        borderRadius: '4px',
                                                        marginRight: '10px',
                                                        cursor: 'pointer'
                                                    }}
                                                >
                                                    Save
                                                </button>
                                                <button
                                                    onClick={() => setEditingId(null)}
                                                    style={{
                                                        padding: '8px 15px',
                                                        backgroundColor: '#6c757d',
                                                        color: 'white',
                                                        border: 'none',
                                                        borderRadius: '4px',
                                                        cursor: 'pointer'
                                                    }}
                                                >
                                                    Cancel
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ) : (
                                    <tr key={student.studentId}>
                                        <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>{student.studentId}</td>
                                        <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>{student.rollNumber}</td>
                                        <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>{student.name}</td>
                                        <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>{student.email}</td>
                                        <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>{student.semester}</td>
                                        <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>
                                            <span style={{
                                                padding: '4px 8px',
                                                borderRadius: '3px',
                                                backgroundColor: student.status === 'Active' ? '#d4edda' : '#f8d7da',
                                                color: student.status === 'Active' ? '#155724' : '#721c24'
                                            }}>
                                                {student.status}
                                            </span>
                                        </td>
                                        <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>
                                            <button
                                                onClick={() => handleEdit(student)}
                                                style={{
                                                    padding: '5px 10px',
                                                    backgroundColor: '#007bff',
                                                    color: 'white',
                                                    border: 'none',
                                                    borderRadius: '3px',
                                                    marginRight: '5px',
                                                    cursor: 'pointer'
                                                }}
                                            >
                                                Edit
                                            </button>
                                            <button
                                                onClick={() => handleDelete(student.studentId)}
                                                style={{
                                                    padding: '5px 10px',
                                                    backgroundColor: '#dc3545',
                                                    color: 'white',
                                                    border: 'none',
                                                    borderRadius: '3px',
                                                    cursor: 'pointer'
                                                }}
                                            >
                                                Delete
                                            </button>
                                        </td>
                                    </tr>
                                )
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}
