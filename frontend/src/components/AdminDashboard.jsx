import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AddStudentForm from './AddStudentForm';
import StudentList from './StudentList';
import AddFacultyForm from './AddFacultyForm';
import FacultyList from './FacultyList';

export default function AdminDashboard({ authData }) {
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState('dashboard');
    const [refreshKey, setRefreshKey] = useState(0);

    const handleLogout = () => {
        localStorage.removeItem('authData') ;
        navigate('/login') ;
    };

    const handleStudentAdded = () => {
        setRefreshKey(prev => prev + 1);
    };

    const handleFacultyAdded = () => {
        setRefreshKey(prev => prev + 1);
    };

    const tabStyle = (isActive) => ({
        padding: '10px 20px',
        backgroundColor: isActive ? '#007bff' : '#e9ecef',
        color: isActive ? 'white' : 'black',
        border: 'none',
        borderRadius: isActive ? '4px 4px 0 0' : '0',
        cursor: 'pointer',
        marginRight: '5px',
        fontSize: '14px',
        fontWeight: isActive ? 'bold' : 'normal'
    });

    return (
        <div style={{ padding: '20px', backgroundColor: '#f8f9fa', minHeight: '100vh' }}>
            {/* Header */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' }}>
                <h2>Admin Dashboard</h2>
                <button 
                    onClick={handleLogout}
                    style={{
                        padding: '8px 16px',
                        backgroundColor: '#dc3545',
                        color: 'white',
                        border: 'none',
                        borderRadius: '4px',
                        cursor: 'pointer'
                    }}
                >
                    Logout
                </button>
            </div>

            {/* User Info Box */}
            {authData && (
                <div style={{ backgroundColor: '#d1ecf1', padding: '15px', borderRadius: '4px', marginBottom: '20px', border: '1px solid #bee5eb' }}>
                    <h4 style={{ margin: '0 0 10px 0' }}>👤 Logged in as: {authData.user?.name || 'Admin'}</h4>
                    <p style={{ margin: '0' }}><strong>Email:</strong> {authData.user?.email || 'N/A'}</p>
                </div>
            )}

            {/* Tab Navigation */}
            <div style={{ marginBottom: '20px', display: 'flex', flexWrap: 'wrap', gap: '5px' }}>
                <button style={tabStyle(activeTab === 'dashboard')} onClick={() => setActiveTab('dashboard')}>
                    📊 Dashboard
                </button>
                <button style={tabStyle(activeTab === 'addStudent')} onClick={() => setActiveTab('addStudent')}>
                    ➕ Add Student
                </button>
                <button style={tabStyle(activeTab === 'viewStudents')} onClick={() => setActiveTab('viewStudents')}>
                    👥 View Students
                </button>
                <button style={tabStyle(activeTab === 'addFaculty')} onClick={() => setActiveTab('addFaculty')}>
                    ➕ Add Faculty
                </button>
                <button style={tabStyle(activeTab === 'viewFaculty')} onClick={() => setActiveTab('viewFaculty')}>
                    👨‍🏫 View Faculty
                </button>
            </div>

            {/* Tab Content */}
            <div style={{ backgroundColor: 'white', borderRadius: '4px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
                {/* Dashboard Tab */}
                {activeTab === 'dashboard' && (
                    <div style={{ padding: '30px' }}>
                        <h3>Welcome to Admin Dashboard</h3>
                        <p>Select a tab above to manage students, faculty, departments, and other resources.</p>
                        
                        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '20px', marginTop: '30px' }}>
                            <div style={{ backgroundColor: '#e7f3ff', padding: '20px', borderRadius: '8px', border: '2px solid #007bff' }}>
                                <h4 style={{ color: '#007bff' }}>📚 Students</h4>
                                <p>Manage student records, admissions, and profiles</p>
                                <button onClick={() => setActiveTab('addStudent')} style={{ backgroundColor: '#007bff', color: 'white', padding: '8px 15px', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                                    Add Student
                                </button>
                            </div>

                            <div style={{ backgroundColor: '#f0f8f0', padding: '20px', borderRadius: '8px', border: '2px solid #28a745' }}>
                                <h4 style={{ color: '#28a745' }}>👨‍🏫 Faculty</h4>
                                <p>Manage faculty members and their assignments</p>
                                <button onClick={() => setActiveTab('addFaculty')} style={{ backgroundColor: '#28a745', color: 'white', padding: '8px 15px', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                                    Add Faculty
                                </button>
                            </div>

                            <div style={{ backgroundColor: '#fff8e7', padding: '20px', borderRadius: '8px', border: '2px solid #ffc107' }}>
                                <h4 style={{ color: '#ff9800' }}>🏢 Departments</h4>
                                <p>Create and manage departments</p>
                                <button onClick={() => setActiveTab('dashboard')} style={{ backgroundColor: '#ff9800', color: 'white', padding: '8px 15px', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                                    Coming Soon
                                </button>
                            </div>
                        </div>
                    </div>
                )}

                {/* Add Student Tab */}
                {activeTab === 'addStudent' && (
                    <div key={`addStudent-${refreshKey}`}>
                        <AddStudentForm onStudentAdded={handleStudentAdded} />
                    </div>
                )}

                {/* View Students Tab */}
                {activeTab === 'viewStudents' && (
                    <div key={`viewStudents-${refreshKey}`}>
                        <StudentList />
                    </div>
                )}

                {/* Add Faculty Tab */}
                {activeTab === 'addFaculty' && (
                    <div key={`addFaculty-${refreshKey}`}>
                        <AddFacultyForm onFacultyAdded={handleFacultyAdded} />
                    </div>
                )}

                {/* View Faculty Tab */}
                {activeTab === 'viewFaculty' && (
                    <div key={`viewFaculty-${refreshKey}`}>
                        <FacultyList />
                    </div>
                )}
            </div>
        </div>
    );
}