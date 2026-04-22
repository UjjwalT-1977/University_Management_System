import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AddStudentForm from './AddStudentForm';
import StudentList from './StudentList';
import AddFacultyForm from './AddFacultyForm' ;
import FacultyList from './FacultyList' ;
import EnrollmentForm from './EnrollmentForm';
import AddDepartmentForm from './AddDepartmentForm';
import ViewDepartmentsForm from './ViewDepartmentsForm';
import AddCourseForm from './AddCourseForm';
import ViewCoursesForm from './ViewCoursesForm';

export default function AdminDashboard({ authData }) {
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState('dashboard') ;
    const [refreshKey, setRefreshKey] = useState(0) ;

    const handleLogout = () => {
        localStorage.removeItem('authData') ;
        navigate('/login') ;
    };

    const handleStudentAdded = () => {
        setRefreshKey(prev => prev + 1) ;
    } ;

    const handleFacultyAdded = () => {
        setRefreshKey(prev => prev + 1) ;
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
    <div className="ums-page ums-shell">
            {/* Header */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' }}>
                <h2 className="ums-title">Admin Dashboard</h2>
                <button className="ums-btn ums-btn--danger" 
                    onClick={handleLogout}
                   
                >
                    Logout
                </button>
            </div>

            {/* User Info Box */}
            {authData && (
                <div style={{ backgroundColor: '#d1ecf1', padding: '15px', borderRadius: '4px', marginBottom: '20px', border: '1px solid #bee5eb' }}>
                    <h4 style={{ margin: '0 0 10px 0' }}>👤 Logged in as: {authData.user?.name || 'Admin'}</h4>
                    <p className="ums-subtitle"><strong>Email:</strong> {authData.user?.email || 'N/A'}</p>
                </div>
            )}

            {/* Tab Navigation */}
            <div style={{ marginBottom: '20px', display: 'flex', flexWrap: 'wrap', gap: '5px' }}>
                <button className={activeTab === 'dashboard' ? 'ums-tab ums-tab--active' : 'ums-tab'}  onClick={() => setActiveTab('dashboard')}>
                    📊 Dashboard
                </button>
                <button className={activeTab === 'addStudent' ? 'ums-tab ums-tab--active' : 'ums-tab'}  onClick={() => setActiveTab('addStudent')}>
                    ➕ Add Student
                </button>
                <button className={activeTab === 'viewStudents' ? 'ums-tab ums-tab--active' : 'ums-tab'}  onClick={() => setActiveTab('viewStudents')}>
                    👥 View Students
                </button>
                <button className={activeTab === 'addFaculty' ? 'ums-tab ums-tab--active' : 'ums-tab'}  onClick={() => setActiveTab('addFaculty')}>
                    ➕ Add Faculty
                </button>
                <button className={activeTab === 'viewFaculty' ? 'ums-tab ums-tab--active' : 'ums-tab'}  onClick={() => setActiveTab('viewFaculty')}>
                    👨‍🏫 View Faculty
                </button>
                <button className={activeTab === 'enrollments' ? 'ums-tab ums-tab--active' : 'ums-tab'}  onClick={() => setActiveTab('enrollments')}>
                    📝 Enroll Students
                </button>
                <button className={activeTab === 'addDepartment' ? 'ums-tab ums-tab--active' : 'ums-tab'}  onClick={() => setActiveTab('addDepartment')}>
                    ➕ Add Department
                </button>
                <button className={activeTab === 'viewDepartments' ? 'ums-tab ums-tab--active' : 'ums-tab'}  onClick={() => setActiveTab('viewDepartments')}>
                    🏢 View Departments
                </button>
                <button className={activeTab === 'addCourse' ? 'ums-tab ums-tab--active' : 'ums-tab'}  onClick={() => setActiveTab('addCourse')}>
                    📚 Add Course
                </button>
                <button className={activeTab === 'viewCourses' ? 'ums-tab ums-tab--active' : 'ums-tab'}  onClick={() => setActiveTab('viewCourses')}>
                    📖 View Courses
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
                                <button className={activeTab === 'addStudent' ? 'ums-tab ums-tab--active' : 'ums-tab'}  onClick={() => setActiveTab('addStudent')}>
                                    Add Student
                                </button>
                            </div>

                            <div style={{ backgroundColor: '#f0f8f0', padding: '20px', borderRadius: '8px', border: '2px solid #28a745' }}>
                                <h4 style={{ color: '#28a745' }}>👨‍🏫 Faculty</h4>
                                <p>Manage faculty members and their assignments</p>
                                <button className={activeTab === 'addFaculty' ? 'ums-tab ums-tab--active' : 'ums-tab'}  onClick={() => setActiveTab('addFaculty')}>
                                    Add Faculty
                                </button>
                            </div>

                            <div style={{ backgroundColor: '#fff8e7', padding: '20px', borderRadius: '8px', border: '2px solid #ffc107' }}>
                                <h4 style={{ color: '#ff9800' }}>🏢 Departments</h4>
                                <p>Create and manage departments</p>
                                <button className={activeTab === 'addDepartment' ? 'ums-tab ums-tab--active' : 'ums-tab'}  onClick={() => setActiveTab('addDepartment')}>
                                    Add Department
                                </button>
                            </div>

                            <div style={{ backgroundColor: '#f0e7f8', padding: '20px', borderRadius: '8px', border: '2px solid #6f42c1' }}>
                                <h4 style={{ color: '#6f42c1' }}>📚 Courses</h4>
                                <p>Create and assign courses to departments</p>
                                <button className={activeTab === 'addCourse' ? 'ums-tab ums-tab--active' : 'ums-tab'}  onClick={() => setActiveTab('addCourse')}>
                                    Add Course
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

                {/* Enrollment Tab */}
                {activeTab === 'enrollments' && (
                    <div style={{ padding: '30px' }}>
                        <EnrollmentForm adminId={authData?.user?.adminId} onEnrollmentSuccess={() => setRefreshKey(prev => prev + 1)} />
                    </div>
                )}

                {/* Add Department Tab */}
                {activeTab === 'addDepartment' && (
                    <div key={`addDepartment-${refreshKey}`} style={{ padding: '30px' }}>
                        <AddDepartmentForm onDepartmentAdded={() => setRefreshKey(prev => prev + 1)} />
                    </div>
                )}

                {/* View Departments Tab */}
                {activeTab === 'viewDepartments' && (
                    <div key={`viewDepartments-${refreshKey}`} style={{ padding: '30px' }}>
                        <ViewDepartmentsForm refreshKey={refreshKey} />
                    </div>
                )}

                {/* Add Course Tab */}
                {activeTab === 'addCourse' && (
                    <div key={`addCourse-${refreshKey}`} style={{ padding: '30px' }}>
                        <AddCourseForm onCourseAdded={() => setRefreshKey(prev => prev + 1)} />
                    </div>
                )}

                {/* View Courses Tab */}
                {activeTab === 'viewCourses' && (
                    <div key={`viewCourses-${refreshKey}`} style={{ padding: '30px' }}>
                        <ViewCoursesForm refreshKey={refreshKey} />
                    </div>
                )}
            </div>
        </div>
    );
}