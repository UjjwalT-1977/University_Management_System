import { useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import axios from 'axios';

export default function StudentDashboard({ authData }) {
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState('overview');
    const [studentInfo, setStudentInfo] = useState(null);
    const [enrolledCourses, setEnrolledCourses] = useState([]);
    const [courseDetails, setCourseDetails] = useState({});
    const [attendance, setAttendance] = useState([]);
    const [marks, setMarks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const studentId = authData?.user?.id || 1; // Fallback ID for testing

    useEffect(() => {
        fetchAllData();
    }, []);

    const fetchAllData = async () => {
        try {
            setLoading(true);
            setError('');

            // Fetch student info
            const studentRes = await axios.get(`http://localhost:8080/api/student/${studentId}`);
            setStudentInfo(studentRes.data.data);

            // Fetch enrolled courses
            const enrollRes = await axios.get(`http://localhost:8080/api/enrollment/student/${studentId}`);
            const enrolledData = enrollRes.data.data || [];
            setEnrolledCourses(enrolledData);

            // Fetch course details for all enrolled courses
            if (enrolledData.length > 0) {
                const courseDetailsMap = {};
                for (const enrollment of enrolledData) {
                    try {
                        const courseRes = await axios.get(`http://localhost:8080/api/course/${enrollment.courseId}`);
                        courseDetailsMap[enrollment.courseId] = courseRes.data.data;
                    } catch (err) {
                        console.error(`Failed to fetch course ${enrollment.courseId}:`, err);
                        courseDetailsMap[enrollment.courseId] = { courseName: 'Unknown Course', courseCode: 'N/A' };
                    }
                }
                setCourseDetails(courseDetailsMap);
            }

            // Fetch attendance
            const attRes = await axios.get(`http://localhost:8080/api/attendance/student/${studentId}`);
            setAttendance(attRes.data.data || []);

            // Fetch marks
            const marksRes = await axios.get(`http://localhost:8080/api/marks/student/${studentId}`);
            setMarks(marksRes.data.data || []);

        } catch (err) {
            setError('Failed to load dashboard data: ' + (err.response?.data?.message || err.message));
            console.error('Dashboard Error:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('authData');
        navigate('/login');
    };

    const refreshMarks = async () => {
        try {
            console.log('Refreshing marks...');
            const marksRes = await axios.get(`http://localhost:8080/api/marks/student/${studentId}`);
            console.log('Refreshed marks:', marksRes.data);
            setMarks(marksRes.data.data || []);
            setError('');
        } catch (err) {
            console.error('Error refreshing marks:', err);
            setError('Failed to refresh marks: ' + err.message);
        }
    };

    const styles = {
        container: { padding: '20px', fontFamily: 'Arial, sans-serif' },
        header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px', borderBottom: '2px solid #007bff', paddingBottom: '15px' },
        tabs: { display: 'flex', gap: '10px', marginBottom: '20px' },
        tab: { padding: '10px 20px', border: '1px solid #ddd', backgroundColor: '#f5f5f5', cursor: 'pointer', borderRadius: '4px' },
        tabActive: { backgroundColor: '#007bff', color: 'white', border: '1px solid #007bff' },
        card: { backgroundColor: '#f9f9f9', padding: '15px', marginBottom: '15px', borderRadius: '4px', border: '1px solid #ddd' },
        Section: { marginBottom: '25px' },
        table: { width: '100%', borderCollapse: 'collapse', backgroundColor: 'white' },
        th: { backgroundColor: '#007bff', color: 'white', padding: '10px', textAlign: 'left' },
        td: { padding: '10px', borderBottom: '1px solid #ddd' },
        button: { padding: '8px 16px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' },
        infoGrid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '15px' },
        infoCard: { backgroundColor: '#e7f3ff', padding: '15px', borderRadius: '4px', border: '1px solid #b3d9ff' }
    };

    if (loading) return <div style={styles.container}><p>Loading dashboard...</p></div>;

    return (
        <div style={styles.container}>
            {/* Header */}
            <div style={styles.header}>
                <h1>Student Dashboard</h1>
                <button style={styles.button} onClick={handleLogout}>Logout</button>
            </div>

            {/* Error Message */}
            {error && <div style={{ padding: '10px', backgroundColor: '#f8d7da', color: '#721c24', borderRadius: '4px', marginBottom: '15px' }}>{error}</div>}

            {/* Tabs */}
            <div style={styles.tabs}>
                <button 
                    style={{ ...styles.tab, ...(activeTab === 'overview' && styles.tabActive) }}
                    onClick={() => setActiveTab('overview')}
                >
                    Overview
                </button>
                <button 
                    style={{ ...styles.tab, ...(activeTab === 'courses' && styles.tabActive) }}
                    onClick={() => setActiveTab('courses')}
                >
                    My Courses
                </button>
                <button 
                    style={{ ...styles.tab, ...(activeTab === 'attendance' && styles.tabActive) }}
                    onClick={() => setActiveTab('attendance')}
                >
                    Attendance
                </button>
                <button 
                    style={{ ...styles.tab, ...(activeTab === 'marks' && styles.tabActive) }}
                    onClick={() => setActiveTab('marks')}
                >
                    Marks & Grades
                </button>
            </div>

            {/* TAB: OVERVIEW */}
            {activeTab === 'overview' && (
                <div style={styles.Section}>
                    <h2>Student Information</h2>
                    {studentInfo && (
                        <div style={styles.infoGrid}>
                            <div style={styles.infoCard}>
                                <strong>Name:</strong> <p>{studentInfo.name}</p>
                            </div>
                            <div style={styles.infoCard}>
                                <strong>Roll Number:</strong> <p>{studentInfo.rollNumber}</p>
                            </div>
                            <div style={styles.infoCard}>
                                <strong>Email:</strong> <p>{studentInfo.email}</p>
                            </div>
                            <div style={styles.infoCard}>
                                <strong>Department ID:</strong> <p>{studentInfo.deptId}</p>
                            </div>
                            <div style={styles.infoCard}>
                                <strong>CGPA:</strong> <p>{studentInfo.cgpa?.toFixed(2) || 'N/A'}</p>
                            </div>
                            <div style={styles.infoCard}>
                                <strong>Status:</strong> <p>{studentInfo.status || 'Active'}</p>
                            </div>
                        </div>
                    )}

                    <h2 style={{ marginTop: '30px' }}>Quick Stats</h2>
                    <div style={styles.infoGrid}>
                        <div style={styles.infoCard}>
                            <strong>Enrolled Courses:</strong> <p style={{ fontSize: '24px', color: '#007bff' }}>{enrolledCourses.length}</p>
                        </div>
                        <div style={styles.infoCard}>
                            <strong>Attendance Records:</strong> <p style={{ fontSize: '24px', color: '#28a745' }}>{attendance.length}</p>
                        </div>
                        <div style={styles.infoCard}>
                            <strong>Marks Records:</strong> <p style={{ fontSize: '24px', color: '#ffc107' }}>{marks.length}</p>
                        </div>
                    </div>
                </div>
            )}

            {/* TAB: MY COURSES */}
            {activeTab === 'courses' && (
                <div style={styles.Section}>
                    <h2>Enrolled Courses</h2>
                    {enrolledCourses.length > 0 ? (
                        <table style={styles.table}>
                            <thead>
                                <tr>
                                    <th style={styles.th}>Course Code</th>
                                    <th style={styles.th}>Course Name</th>
                                    <th style={styles.th}>Enrollment Date</th>
                                    <th style={styles.th}>CGPA at Enrollment</th>
                                    <th style={styles.th}>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                {enrolledCourses.map((course) => {
                                    const courseInfo = courseDetails[course.courseId] || {};
                                    return (
                                        <tr key={course.enrollmentId}>
                                            <td style={styles.td}>{courseInfo.courseCode || 'N/A'}</td>
                                            <td style={styles.td}>{courseInfo.courseName || 'Unknown Course'}</td>
                                            <td style={styles.td}>{course.enrollmentDate}</td>
                                            <td style={styles.td}>{course.cgpaAtEnrollment?.toFixed(2)}</td>
                                            <td style={styles.td}>
                                                <span style={{ padding: '4px 8px', borderRadius: '4px', backgroundColor: course.status === 'Enrolled' ? '#d4edda' : '#f8d7da', color: course.status === 'Enrolled' ? '#155724' : '#721c24' }}>
                                                    {course.status}
                                                </span>
                                            </td>
                                        </tr>
                                    );
                                })}
                            </tbody>
                        </table>
                    ) : (
                        <p>No courses enrolled yet.</p>
                    )}
                </div>
            )}

            {/* TAB: ATTENDANCE */}
            {activeTab === 'attendance' && (
                <div style={styles.Section}>
                    <h2>Attendance Records</h2>
                    {attendance.length > 0 ? (
                        <table style={styles.table}>
                            <thead>
                                <tr>
                                    <th style={styles.th}>Attendance ID</th>
                                    <th style={styles.th}>Course ID</th>
                                    <th style={styles.th}>Date</th>
                                    <th style={styles.th}>Status</th>
                                    <th style={styles.th}>Recorded By</th>
                                </tr>
                            </thead>
                            <tbody>
                                {attendance.map((att) => (
                                    <tr key={att.attendanceId}>
                                        <td style={styles.td}>{att.attendanceId}</td>
                                        <td style={styles.td}>{att.courseId}</td>
                                        <td style={styles.td}>{att.date}</td>
                                        <td style={styles.td}>
                                            <span style={{ padding: '4px 8px', borderRadius: '4px', backgroundColor: att.status === 'Present' ? '#d4edda' : att.status === 'Leave' ? '#cfe2ff' : '#f8d7da', color: att.status === 'Present' ? '#155724' : att.status === 'Leave' ? '#0c5460' : '#721c24' }}>
                                                {att.status}
                                            </span>
                                        </td>
                                        <td style={styles.td}>{att.recordedBy}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    ) : (
                        <p>No attendance records yet.</p>
                    )}
                </div>
            )}

            {/* TAB: MARKS & GRADES */}
            {activeTab === 'marks' && (
                <div style={styles.Section}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '15px' }}>
                        <h2 style={{ margin: 0 }}>Marks & Grades</h2>
                        <button 
                            onClick={refreshMarks}
                            style={{ padding: '8px 16px', backgroundColor: '#17a2b8', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}
                        >
                            🔄 Refresh Marks
                        </button>
                    </div>
                    {marks.length > 0 ? (
                        <div style={{ overflowX: 'auto' }}>
                            <table style={styles.table}>
                                <thead>
                                    <tr>
                                        <th style={styles.th}>Course Code</th>
                                        <th style={styles.th}>Course Name</th>
                                        <th style={styles.th} title="Internal Assessment (Max 30)">Internal⁕</th>
                                        <th style={styles.th} title="External Exam (Max 70)">External⁕</th>
                                        <th style={styles.th} title="Total Marks (Max 100)">Total⁕</th>
                                        <th style={styles.th}>Percentage</th>
                                        <th style={styles.th}>Grade</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {marks.map((mark) => {
                                        const courseInfo = courseDetails[mark.courseId] || {};
                                        const percentage = ((mark.totalMarks / 100) * 100).toFixed(2);
                                        
                                        // Grade color coding: A=Green, B/C=Yellow, D/F=Red
                                        let gradeColor = '#ffffff';
                                        let gradeTextColor = '#000000';
                                        if (mark.grade === 'A') {
                                            gradeColor = '#28a745';
                                            gradeTextColor = 'white';
                                        } else if (mark.grade === 'B') {
                                            gradeColor = '#ffc107';
                                            gradeTextColor = 'black';
                                        } else if (mark.grade === 'C') {
                                            gradeColor = '#ffc107';
                                            gradeTextColor = 'black';
                                        } else if (mark.grade === 'D') {
                                            gradeColor = '#fd7e14';
                                            gradeTextColor = 'white';
                                        } else if (mark.grade === 'F') {
                                            gradeColor = '#dc3545';
                                            gradeTextColor = 'white';
                                        }
                                        
                                        return (
                                            <tr key={mark.marksId} style={{ borderLeft: `5px solid ${gradeColor}` }}>
                                                <td style={styles.td}><strong>{courseInfo.courseCode || 'N/A'}</strong></td>
                                                <td style={styles.td}>{courseInfo.courseName || 'Unknown Course'}</td>
                                                <td style={{ ...styles.td, textAlign: 'center' }}>{mark.internalMarks?.toFixed(1) || 0}/30</td>
                                                <td style={{ ...styles.td, textAlign: 'center' }}>{mark.externalMarks?.toFixed(1) || 0}/70</td>
                                                <td style={{ ...styles.td, textAlign: 'center', fontWeight: 'bold' }}>{mark.totalMarks?.toFixed(1) || 0}/100</td>
                                                <td style={{ ...styles.td, textAlign: 'center' }}>
                                                    <strong>{percentage}%</strong>
                                                </td>
                                                <td style={{ ...styles.td, backgroundColor: gradeColor, color: gradeTextColor, textAlign: 'center', fontWeight: 'bold', borderRadius: '4px' }}>
                                                    {mark.grade || 'N/A'}
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                            <p style={{ fontSize: '12px', color: '#666', marginTop: '10px' }}>
                                ⁕ Internal (0-30) + External (0-70) = Total (0-100)<br/>
                                Grade: A ≥ 90 | B ≥ 80 | C ≥ 70 | D ≥ 60 | F &lt; 60
                            </p>
                        </div>
                    ) : (
                        <p>No marks recorded yet.</p>
                    )}
                </div>
            )}
        </div>
    );
}