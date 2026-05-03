import { useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import axios from 'axios';
import '../App.css'; 

export default function StudentDashboard({ authData }) {
    const navigate = useNavigate();
    
    // ================= STATE =================
    const [activeTab, setActiveTab] = useState('overview');
    const [studentInfo, setStudentInfo] = useState(null);
    const [enrolledCourses, setEnrolledCourses] = useState([]);
    const [courseDetails, setCourseDetails] = useState({});
    const [attendance, setAttendance] = useState([]);
    const [marks, setMarks] = useState([]);
    const [allCourses, setAllCourses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [availableQuizzes, setAvailableQuizzes] = useState([]);

    const studentId = authData?.user?.id || 1; 

    // ================= DATA FETCHING =================
    useEffect(() => {
        fetchAllData();
    }, []);

    const fetchAllData = async () => {
        try {
            setLoading(true);
            setError('');

            const studentRes = await axios.get(`http://localhost:8080/api/student/${studentId}`);
            setStudentInfo(studentRes.data.data);

            const enrollRes = await axios.get(`http://localhost:8080/api/enrollment/student/${studentId}`);
            const enrolledData = enrollRes.data.data || [];
            setEnrolledCourses(enrolledData);

            const allCoursesRes = await axios.get(`http://localhost:8080/api/course/all`);
            setAllCourses(allCoursesRes.data.data || []);

            // Fetch Course Details AND Quizzes
            if (enrolledData.length > 0) {
                const courseDetailsMap = {};
                let fetchedQuizzes = [];

                for (const enrollment of enrolledData) {
                    try {
                        const courseRes = await axios.get(`http://localhost:8080/api/course/${enrollment.courseId}`);
                        courseDetailsMap[enrollment.courseId] = courseRes.data.data;

                        // Fetch quizzes for this specific enrolled course
                        const quizRes = await axios.get(`http://localhost:8080/api/quiz/course/${enrollment.courseId}`);
                        if (quizRes.data.data && quizRes.data.data.length > 0) {
                            const quizzesWithCourseInfo = quizRes.data.data.map(q => ({
                                ...q,
                                courseName: courseRes.data.data.courseName,
                                courseCode: courseRes.data.data.courseCode
                            }));
                            fetchedQuizzes = [...fetchedQuizzes, ...quizzesWithCourseInfo];
                        }

                    } catch (err) {
                        console.error(`Failed to fetch course data for ${enrollment.courseId}:`, err);
                    }
                }
                setCourseDetails(courseDetailsMap);
                setAvailableQuizzes(fetchedQuizzes);
            }

            const attRes = await axios.get(`http://localhost:8080/api/attendance/student/${studentId}`);
            setAttendance(attRes.data.data || []);

            const marksRes = await axios.get(`http://localhost:8080/api/marks/student/${studentId}`);
            setMarks(marksRes.data.data || []);

        } catch (err) {
            setError('Failed to load data: ' + (err.response?.data?.message || err.message));
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
            const marksRes = await axios.get(`http://localhost:8080/api/marks/student/${studentId}`);
            setMarks(marksRes.data.data || []);
            setError('');
        } catch (err) {
            setError('Failed to refresh marks: ' + err.message);
        }
    };

    const getGradeBadgeClass = (grade) => {
        if (grade === 'A') return 'ums-badge--success';
        if (grade === 'B') return 'ums-badge--info';
        if (grade === 'C') return 'ums-badge--warning';
        return 'ums-badge--danger';
    };

    if (loading) return (
        <div className="ums-page flex-center" style={{ minHeight: '100vh' }}>
            <h2 style={{ color: 'var(--color-primary)' }}>Loading dashboard...</h2>
        </div>
    );

    return (
        <div className="ums-page">
            <div className="ums-shell">
                
                {/* Header Area */}
                <header className="ums-dashboard-header">
                    <div>
                        <h1>📚 Student Dashboard</h1>
                        <p className="mb-0 text-left">Welcome to your academic portal</p>
                    </div>
                    <button className="ums-btn ums-btn--danger" onClick={handleLogout}>
                        🚪 Logout
                    </button>
                </header>

                {error && <div className="ums-alert ums-alert--error mt-3">⚠️ {error}</div>}

                {/* Tab Navigation */}
                <div className="ums-tabs mt-4 mb-4" style={{ flexWrap: 'wrap' }}>
                    <button className={`ums-tab ${activeTab === 'overview' ? 'ums-tab--active' : ''}`} onClick={() => setActiveTab('overview')}>Overview</button>
                    <button className={`ums-tab ${activeTab === 'courses' ? 'ums-tab--active' : ''}`} onClick={() => setActiveTab('courses')}>My Courses</button>
                    <button className={`ums-tab ${activeTab === 'availableCourses' ? 'ums-tab--active' : ''}`} onClick={() => setActiveTab('availableCourses')}>Available Courses</button>
                    <button className={`ums-tab ${activeTab === 'attendance' ? 'ums-tab--active' : ''}`} onClick={() => setActiveTab('attendance')}>Attendance</button>
                    <button className={`ums-tab ${activeTab === 'marks' ? 'ums-tab--active' : ''}`} onClick={() => setActiveTab('marks')}>Marks & Grades</button>
                    <button className={`ums-tab ${activeTab === 'quizzes' ? 'ums-tab--active' : ''}`} onClick={() => setActiveTab('quizzes')}>📝 My Quizzes</button>
                </div>

                {/* ================= OVERVIEW TAB ================= */}
                {activeTab === 'overview' && (
                    <div className="ums-panel text-left">
                        <h2 className="mb-3">Student Information</h2>
                        
                        {studentInfo && (
                            <div className="ums-info-box-grid mb-4">
                                <div className="ums-info-item">
                                    <span className="ums-info-item-label">Name</span>
                                    <span className="ums-info-item-value">{studentInfo.name}</span>
                                </div>
                                <div className="ums-info-item">
                                    <span className="ums-info-item-label">Roll Number</span>
                                    <span className="ums-info-item-value">{studentInfo.rollNumber}</span>
                                </div>
                                <div className="ums-info-item">
                                    <span className="ums-info-item-label">Email</span>
                                    <span className="ums-info-item-value">{studentInfo.email}</span>
                                </div>
                                <div className="ums-info-item">
                                    <span className="ums-info-item-label">Department ID</span>
                                    <span className="ums-info-item-value">{studentInfo.deptId}</span>
                                </div>
                                <div className="ums-info-item">
                                    <span className="ums-info-item-label">CGPA</span>
                                    <span className="ums-info-item-value">{studentInfo.cgpa?.toFixed(2) || 'N/A'}</span>
                                </div>
                                <div className="ums-info-item">
                                    <span className="ums-info-item-label">Status</span>
                                    <span className="ums-badge ums-badge--success mt-1" style={{ width: 'fit-content' }}>
                                        {studentInfo.status || 'Active'}
                                    </span>
                                </div>
                            </div>
                        )}

                        <h2 className="mt-4 mb-3">Quick Stats</h2>
                        <div className="ums-stats-grid">
                            <div className="ums-stat-card" style={{ borderTop: '4px solid var(--color-primary)' }}>
                                <span className="ums-stat-label">Enrolled Courses</span>
                                <span className="ums-stat-value">{enrolledCourses.length}</span>
                            </div>
                            <div className="ums-stat-card" style={{ borderTop: '4px solid var(--color-success)' }}>
                                <span className="ums-stat-label">Attendance Records</span>
                                <span className="ums-stat-value">{attendance.length}</span>
                            </div>
                            <div className="ums-stat-card" style={{ borderTop: '4px solid var(--color-warning)' }}>
                                <span className="ums-stat-label">Marks Records</span>
                                <span className="ums-stat-value">{marks.length}</span>
                            </div>
                        </div>
                    </div>
                )}

                {/* ================= ENROLLED COURSES TAB ================= */}
                {activeTab === 'courses' && (
                    <div className="ums-panel text-left">
                        <h2 className="mb-3">Enrolled Courses</h2>
                        {enrolledCourses.length > 0 ? (
                            <div className="ums-table-container">
                                <table className="ums-table">
                                    <thead>
                                        <tr>
                                            <th>Course Code</th>
                                            <th>Course Name</th>
                                            <th>Enrollment Date</th>
                                            <th>CGPA at Enrollment</th>
                                            <th>Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {enrolledCourses.map((course) => {
                                            const courseInfo = courseDetails[course.courseId] || {};
                                            return (
                                                <tr key={course.enrollmentId}>
                                                    <td><strong>{courseInfo.courseCode || 'N/A'}</strong></td>
                                                    <td>{courseInfo.courseName || 'Unknown Course'}</td>
                                                    <td>{course.enrollmentDate}</td>
                                                    <td>{course.cgpaAtEnrollment?.toFixed(2)}</td>
                                                    <td>
                                                        <span className={`ums-badge ${course.status === 'Enrolled' ? 'ums-badge--success' : 'ums-badge--danger'}`}>
                                                            {course.status}
                                                        </span>
                                                    </td>
                                                </tr>
                                            );
                                        })}
                                    </tbody>
                                </table>
                            </div>
                        ) : (
                            <p className="text-left">No courses enrolled yet.</p>
                        )}
                    </div>
                )}

                {/* ================= AVAILABLE COURSES TAB ================= */}
                {activeTab === 'availableCourses' && (
                    <div className="ums-panel text-left">
                        <h2 className="mb-3">Available Courses</h2>
                        <div className="ums-alert ums-alert--info mb-4">
                            📌 Note: To enroll in a course, please contact your admin.
                        </div>
                        
                        {allCourses && allCourses.length > 0 ? (
                            <div className="ums-grid">
                                {allCourses.map((course) => {
                                    const isEnrolled = enrolledCourses.some(ec => ec.courseId === course.courseId);
                                    return (
                                        <div key={course.courseId} className={`ums-card ${isEnrolled ? 'ums-panel--light' : ''}`}>
                                            <div className="flex-between mb-3">
                                                <div>
                                                    <h4 className="mb-1">{course.courseName}</h4>
                                                    <span className="ums-badge ums-badge--gray">{course.courseCode}</span>
                                                </div>
                                                {isEnrolled && (
                                                    <span className="ums-badge ums-badge--success">✓ Enrolled</span>
                                                )}
                                            </div>
                                            <div className="ums-info-item mt-3 pt-3" style={{ borderTop: '1px solid var(--color-border)' }}>
                                                <div className="flex-between mb-2">
                                                    <span className="ums-info-item-label">Credits</span>
                                                    <strong>{course.credits}</strong>
                                                </div>
                                                <div className="flex-between mb-2">
                                                    <span className="ums-info-item-label">Capacity</span>
                                                    <strong>{course.maxCapacity}</strong>
                                                </div>
                                                <div className="flex-between mb-1">
                                                    <span className="ums-info-item-label">Semester / Year</span>
                                                    <strong>{course.semester} / {course.year}</strong>
                                                </div>
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                        ) : (
                            <p className="text-left">No courses available at the moment.</p>
                        )}
                    </div>
                )}

                {/* ================= ATTENDANCE TAB ================= */}
                {activeTab === 'attendance' && (
                    <div className="ums-panel text-left">
                        <h2 className="mb-3">Attendance Records</h2>
                        {attendance.length > 0 ? (
                            <div className="ums-table-container">
                                <table className="ums-table">
                                    <thead>
                                        <tr>
                                            <th>Course ID</th>
                                            <th>Date</th>
                                            <th>Status</th>
                                            <th>Recorded By</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {attendance.map((att) => (
                                            <tr key={att.attendanceId}>
                                                <td><strong>{att.courseId}</strong></td>
                                                <td>{att.date || att.attendanceDate}</td>
                                                <td>
                                                    <span className={`ums-badge ${att.status === 'Present' ? 'ums-badge--success' : att.status === 'Leave' ? 'ums-badge--warning' : 'ums-badge--danger'}`}>
                                                        {att.status}
                                                    </span>
                                                </td>
                                                <td>{att.recordedBy || 'System'}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        ) : (
                            <p className="text-left">No attendance records yet.</p>
                        )}
                    </div>
                )}

                {/* ================= MARKS TAB ================= */}
                {activeTab === 'marks' && (
                    <div className="ums-panel text-left">
                        <div className="flex-between mb-4">
                            <h2 className="mb-0">Marks & Grades</h2>
                            <button className="ums-btn ums-btn--primary" onClick={refreshMarks}>
                                🔄 Refresh
                            </button>
                        </div>
                        {marks.length > 0 ? (
                            <div className="ums-table-container">
                                <table className="ums-table">
                                    <thead>
                                        <tr>
                                            <th>Course Code</th>
                                            <th>Course Name</th>
                                            <th className="text-center" title="Internal Assessment (Max 30)">Internal⁕</th>
                                            <th className="text-center" title="External Exam (Max 70)">External⁕</th>
                                            <th className="text-center" title="Total Marks (Max 100)">Total⁕</th>
                                            <th className="text-center">Percentage</th>
                                            <th className="text-center">Grade</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {marks.map((mark) => {
                                            const courseInfo = courseDetails[mark.courseId] || {};
                                            const percentage = mark.totalMarks ? ((mark.totalMarks / 100) * 100).toFixed(2) : 0;
                                            const badgeClass = getGradeBadgeClass(mark.grade);
                                            
                                            return (
                                                <tr key={mark.marksId}>
                                                    <td><strong>{courseInfo.courseCode || 'N/A'}</strong></td>
                                                    <td>{courseInfo.courseName || 'Unknown Course'}</td>
                                                    <td className="text-center">{mark.internalMarks?.toFixed(1) || 0}/30</td>
                                                    <td className="text-center">{mark.externalMarks?.toFixed(1) || 0}/70</td>
                                                    <td className="text-center"><strong>{mark.totalMarks?.toFixed(1) || 0}/100</strong></td>
                                                    <td className="text-center"><strong>{percentage}%</strong></td>
                                                    <td className="text-center">
                                                        <span className={`ums-badge ${badgeClass}`}>
                                                            {mark.grade || 'N/A'}
                                                        </span>
                                                    </td>
                                                </tr>
                                            );
                                        })}
                                    </tbody>
                                </table>
                                <div className="ums-alert ums-alert--info mt-3 mb-0 p-3">
                                    <strong>⁕ Grading Scale Note:</strong> Internal (0-30) + External (0-70) = Total (0-100). Typical Grade Boundaries: A ≥ 90 | B ≥ 80 | C ≥ 70 | D ≥ 60 | F &lt; 60
                                </div>
                            </div>
                        ) : (
                            <p className="text-left">No marks recorded yet.</p>
                        )}
                    </div>
                )}

                {/* ================= NEW QUIZZES TAB CONTENT ================= */}
                {activeTab === 'quizzes' && (
                    <div className="ums-panel text-left">
                        <h2 className="mb-3">Available Quizzes & Tests</h2>
                        
                        {availableQuizzes.length > 0 ? (
                            <div className="ums-grid">
                                {availableQuizzes.map((quiz) => (
                                    <div key={quiz.quizId} className="ums-card" style={{ borderTop: '4px solid #007bff' }}>
                                        <div className="flex-between mb-3">
                                            <div>
                                                <h4 className="mb-1">{quiz.title}</h4>
                                                <span className="ums-badge ums-badge--gray">{quiz.courseCode} - {quiz.courseName}</span>
                                            </div>
                                            <span className="ums-badge ums-badge--info">⏱ {quiz.durationMinutes} mins</span>
                                        </div>
                                        
                                        <div className="mt-4">
                                            <button 
                                                className="ums-btn ums-btn--primary w-100" 
                                                onClick={() => navigate(`/take-quiz/${quiz.quizId}`, { state: { duration: quiz.durationMinutes, title: quiz.title } })}
                                            >
                                                ▶ Start Quiz
                                            </button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="ums-alert ums-alert--info">
                                🎉 No pending quizzes! Your professors haven't assigned any active quizzes for your enrolled courses yet.
                            </div>
                        )}
                    </div>
                )}

            </div>
        </div>
    );
}