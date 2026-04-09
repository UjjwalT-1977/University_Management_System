import { useState, useEffect } from 'react';
import axios from 'axios';

export default function FacultyList() {
    const [faculty, setFaculty] = useState([]);
    const [loading, setLoading] = useState(true);
    const [message, setMessage] = useState('');
    const [messageType, setMessageType] = useState('');
    const [editingId, setEditingId] = useState(null);
    const [editData, setEditData] = useState(null);

    useEffect(() => {
        fetchFaculty();
    }, []);

    const fetchFaculty = async () => {
        setLoading(true);
        try {
            const response = await axios.get('http://localhost:8080/api/faculty/all');
            if (response.data.status === 'success') {
                setFaculty(response.data.data || []);
            }
        } catch (error) {
            console.error('❌ Fetch Faculty Error:', error);
            console.error('Error Details:', error.response?.data || error.message);
            setMessageType('error');
            setMessage('Failed to load faculty: ' + (error.message || 'Unknown error'));
        } finally {
            setLoading(false);
        }
    };

    const handleEdit = (fac) => {
        setEditingId(fac.facultyId);
        setEditData({ ...fac });
    };

    const handleSaveEdit = async () => {
        try {
            const response = await axios.put(
                `http://localhost:8080/api/faculty/update/${editingId}`,
                editData
            );
            
            if (response.data.status === 'success') {
                setMessageType('success');
                setMessage('✓ Faculty updated successfully!');
                setEditingId(null);
                fetchFaculty();
                setTimeout(() => setMessage(''), 3000);
            }
        } catch (error) {
            setMessageType('error');
            setMessage('✗ Failed to update faculty');
        }
    };

    const handleDelete = async (facultyId) => {
        if (window.confirm('Are you sure you want to delete this faculty member?')) {
            try {
                const response = await axios.delete(`http://localhost:8080/api/faculty/delete/${facultyId}`);
                
                if (response.data.status === 'success') {
                    setMessageType('success');
                    setMessage('✓ Faculty deleted successfully!');
                    fetchFaculty();
                    setTimeout(() => setMessage(''), 3000);
                }
            } catch (error) {
                setMessageType('error');
                setMessage('✗ Failed to delete faculty');
            }
        }
    };

    if (loading) {
        return <div style={{ textAlign: 'center', padding: '40px' }}>Loading faculty...</div>;
    }

    return (
        <div style={{ padding: '20px' }}>
            <h2>Faculty Management</h2>

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

            {faculty.length === 0 ? (
                <p>No faculty found. <a href="#" onClick={() => window.location.reload()}>Refresh</a></p>
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
                                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Emp ID</th>
                                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Name</th>
                                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Email</th>
                                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Qualification</th>
                                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Status</th>
                                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {faculty.map(fac => (
                                editingId === fac.facultyId ? (
                                    <tr key={fac.facultyId} style={{ backgroundColor: '#fff3cd' }}>
                                        <td colSpan="7" style={{ padding: '15px' }}>
                                            <h4>Edit Faculty</h4>
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
                                                    <label>Qualification:</label>
                                                    <input
                                                        type="text"
                                                        value={editData.qualification}
                                                        onChange={(e) => setEditData({ ...editData, qualification: e.target.value })}
                                                        style={{ width: '100%', padding: '5px', borderRadius: '3px', border: '1px solid #ddd' }}
                                                    />
                                                </div>
                                                <div>
                                                    <label>Specialization:</label>
                                                    <input
                                                        type="text"
                                                        value={editData.specialization}
                                                        onChange={(e) => setEditData({ ...editData, specialization: e.target.value })}
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
                                                        <option>Leave</option>
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
                                    <tr key={fac.facultyId}>
                                        <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>{fac.facultyId}</td>
                                        <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>{fac.empId}</td>
                                        <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>{fac.name}</td>
                                        <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>{fac.email}</td>
                                        <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>{fac.qualification}</td>
                                        <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>
                                            <span style={{
                                                padding: '4px 8px',
                                                borderRadius: '3px',
                                                backgroundColor: fac.status === 'Active' ? '#d4edda' : '#f8d7da',
                                                color: fac.status === 'Active' ? '#155724' : '#721c24'
                                            }}>
                                                {fac.status}
                                            </span>
                                        </td>
                                        <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>
                                            <button
                                                onClick={() => handleEdit(fac)}
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
                                                onClick={() => handleDelete(fac.facultyId)}
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
