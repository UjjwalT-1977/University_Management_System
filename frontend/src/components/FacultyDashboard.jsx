import { useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import axios from 'axios';

export default function FacultyDashboard({ authData }) {
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState('overview');
    const [facultyInfo, setFacultyInfo] = useState(null);
    const [assignedCourses, setAssignedCourses] = useState([]);
    const [selectedCourse, setSelectedCourse] = useState(null);
    const [courseStudents, setCourseStudents] = useState([]);
    const [courseAttendance, setCourseAttendance] = useState([]);
    const [courseMarks, setCourseMarks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [markForm, setMarkForm] = useState({ studentId: '', internalMarks: '', externalMarks: '' });
    const [studentNames, setStudentNames] = useState({});
    const [attendanceForm, setAttendanceForm] = useState({ studentId: '', date: new Date().toISOString().split('T')[0], status: 'Present' });
    const [editingAttendanceId, setEditingAttendanceId] = useState(null);

    const facultyId = authData?.user?.id || 1; // Fallback ID for testing

    useEffect(() => {
        fetchFacultyData();
    }, []);

    useEffect(() => {
        if (selectedCourse) {
            fetchCourseDetails(selectedCourse);
        }
    }, [selectedCourse]);

    const fetchFacultyData = async () => {
        try {
            setLoading(true);
            setError('');

            // Fetch faculty info
            const facRes = await axios.get(`http://localhost:8080/api/faculty/${facultyId}`);
            setFacultyInfo(facRes.data.data);

            // Fetch courses taught by this faculty
            const coursesRes = await axios.get(`http://localhost:8080/api/course/faculty/${facultyId}`);
            setAssignedCourses(coursesRes.data.data || []);

        } catch (err) {
            setError('Failed to load faculty data: ' + (err.response?.data?.message || err.message));
            console.error('Faculty Error:', err);
        } finally {
            setLoading(false);
        }
    };

    const fetchCourseDetails = async (courseId) => {
        try {
            // Fetch students enrolled in this course
            const studentsRes = await axios.get(`http://localhost:8080/api/enrollment/course/${courseId}`);
            const studentsData = studentsRes.data.data || [];
            setCourseStudents(studentsData);

            // Fetch student names
            const namesMap = {};
            for (const enrollment of studentsData) {
                try {
                    const studentRes = await axios.get(`http://localhost:8080/api/student/${enrollment.studentId}`);
                    namesMap[enrollment.studentId] = studentRes.data.data.name || `Student ${enrollment.studentId}`;
                } catch (err) {
                    namesMap[enrollment.studentId] = `Student ${enrollment.studentId}`;
                }
            }
            setStudentNames(namesMap);

            // Fetch attendance for this course
            const attRes = await axios.get(`http://localhost:8080/api/attendance/course/${courseId}`);
            setCourseAttendance(attRes.data.data || []);

            // Fetch marks for this course
            const marksRes = await axios.get(`http://localhost:8080/api/marks/course/${courseId}`);
            setCourseMarks(marksRes.data.data || []);

        } catch (err) {
            setError('Failed to load course details: ' + err.message);
        }
    };

    const handleRecordMarks = async () => {
        try {
            if (!markForm.studentId || markForm.internalMarks === '' || markForm.externalMarks === '') {
                setError('All mark fields are required');
                return;
            }

            const internal = parseFloat(markForm.internalMarks);
            const external = parseFloat(markForm.externalMarks);

            // Validate ranges
            if (internal < 0 || internal > 30) {
                setError('Internal marks must be between 0 and 30');
                return;
            }
            if (external < 0 || external > 70) {
                setError('External marks must be between 0 and 70');
                return;
            }

            const totalMarks = internal + external;

            // Calculate grade
            let grade = 'F';
            if (totalMarks >= 90) grade = 'A';
            else if (totalMarks >= 80) grade = 'B';
            else if (totalMarks >= 70) grade = 'C';
            else if (totalMarks >= 60) grade = 'D';

            console.log('Recording marks:', { studentId: markForm.studentId, courseId: selectedCourse, internal, external, totalMarks, grade });

            const response = await axios.post(`http://localhost:8080/api/marks/add`, {
                studentId: parseInt(markForm.studentId),
                courseId: parseInt(selectedCourse),
                internalMarks: internal,
                externalMarks: external,
                totalMarks: totalMarks,
                grade: grade,
                recordedBy: facultyId
            });

            console.log('Marks API Response:', response.data);

            if (response.data.status === 'success') {
                setError('');
                setMarkForm({ studentId: '', internalMarks: '', externalMarks: '' });
                
                // Force a delay to ensure DB is updated, then refresh
                setTimeout(() => {
                    fetchCourseDetails(selectedCourse);
                    alert('✓ Marks recorded successfully!');
                }, 500);
            } else {
                setError(response.data.message || 'Failed to record marks');
            }
        } catch (err) {
            console.error('Marks recording error:', err);
            const errorMsg = err.response?.data?.message || err.message;
            setError('Failed to record marks: ' + errorMsg);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('authData');
        navigate('/login');
    };

    const handleMarkAttendance = async () => {
        try {
            if (!attendanceForm.studentId || !attendanceForm.date || !attendanceForm.status) {
                setError('All attendance fields are required');
                return;
            }

            console.log('Recording/Updating attendance:', { 
                attendanceId: editingAttendanceId,
                studentId: attendanceForm.studentId, 
                courseId: selectedCourse, 
                date: attendanceForm.date, 
                status: attendanceForm.status 
            });

            let response;
            if (editingAttendanceId) {
                // Update existing attendance
                response = await axios.put(`http://localhost:8080/api/attendance/update/${editingAttendanceId}`, {
                    studentId: parseInt(attendanceForm.studentId),
                    courseId: parseInt(selectedCourse),
                    date: attendanceForm.date,
                    status: attendanceForm.status,
                    recordedBy: facultyId
                });
            } else {
                // Mark new attendance
                response = await axios.post(`http://localhost:8080/api/attendance/mark`, {
                    studentId: parseInt(attendanceForm.studentId),
                    courseId: parseInt(selectedCourse),
                    date: attendanceForm.date,
                    status: attendanceForm.status,
                    recordedBy: facultyId
                });
            }

            console.log('Attendance API Response:', response.data);

            if (response.data.status === 'success') {
                setError('');
                setAttendanceForm({ studentId: '', date: new Date().toISOString().split('T')[0], status: 'Present' });
                setEditingAttendanceId(null);
                fetchCourseDetails(selectedCourse);
                alert(editingAttendanceId ? '✓ Attendance updated successfully!' : '✓ Attendance marked successfully!');
            } else {
                setError(response.data.message || 'Failed to record attendance');
            }
        } catch (err) {
            console.error('Attendance recording error:', err);
            const errorMsg = err.response?.data?.message || err.message;
            setError('Failed to record attendance: ' + errorMsg);
        }
    };

    const handleEditAttendance = (attendance) => {
        setAttendanceForm({
            studentId: attendance.studentId,
            date: attendance.date,
            status: attendance.status
        });
        setEditingAttendanceId(attendance.attendanceId);
        setError('');
    };

    const handleCancelEdit = () => {
        setAttendanceForm({ studentId: '', date: new Date().toISOString().split('T')[0], status: 'Present' });
        setEditingAttendanceId(null);
        setError('');
    };

    const styles = {
        container: { padding: '20px', fontFamily: 'Arial, sans-serif' },
        header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px', borderBottom: '2px solid #28a745', paddingBottom: '15px' },
        tabs: { display: 'flex', gap: '10px', marginBottom: '20px' },
        tab: { padding: '10px 20px', border: '1px solid #ddd', backgroundColor: '#f5f5f5', cursor: 'pointer', borderRadius: '4px' },
        tabActive: { backgroundColor: '#28a745', color: 'white', border: '1px solid #28a745' },
        card: { backgroundColor: '#f9f9f9', padding: '15px', marginBottom: '15px', borderRadius: '4px', border: '1px solid #ddd' },
        Section: { marginBottom: '25px' },
        table: { width: '100%', borderCollapse: 'collapse', backgroundColor: 'white' },
        th: { backgroundColor: '#28a745', color: 'white', padding: '10px', textAlign: 'left' },
        td: { padding: '10px', borderBottom: '1px solid #ddd' },
        button: { padding: '8px 16px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' },
        infoGrid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '15px' },
        infoCard: { backgroundColor: '#e8f5e9', padding: '15px', borderRadius: '4px', border: '1px solid #a5d6a7' }
    };

    if (loading) return <div style={styles.container}><p className="ums-subtitle">Loading dashboard...</p></div>;

    return (
    <div className="ums-page ums-shell">
            {/* Header */}
            <div style={styles.header}>
                <h1 className="ums-title">Faculty Dashboard</h1>
                <button className="ums-btn ums-btn--danger" onClick={handleLogout}>Logout</button>
            </div>

            {/* Error Message */}
            {error && (
                <div style={{ 
                    padding: '12px 15px', 
                    backgroundColor: '#f8d7da', 
                    color: '#721c24', 
                    borderRadius: '4px', 
                    marginBottom: '15px',
                    border: '1px solid #f5c6cb',
                    fontSize: '14px',
                    fontWeight: 'bold'
                }}>
                    ❌ {error}
                </div>
            )}

            {/* Tabs */}
            <div style={styles.tabs}>
                <button className={activeTab === 'overview' ? 'ums-tab ums-tab--active' : 'ums-tab'} 
                   
                    
                 onClick={() => setActiveTab('overview')}>
                    Overview
                </button>
                <button className={activeTab === 'courses' ? 'ums-tab ums-tab--active' : 'ums-tab'} 
                   
                    
                 onClick={() => setActiveTab('courses')}>
                    My Courses
                </button>
                <button className={activeTab === 'students' ? 'ums-tab ums-tab--active' : 'ums-tab'} 
                   
                    
                 onClick={() => setActiveTab('students')}>
                    Course Students
                </button>
                <button className={activeTab === 'attendance' ? 'ums-tab ums-tab--active' : 'ums-tab'} 
                   
                    
                 onClick={() => setActiveTab('attendance')}>
                    Course Attendance
                </button>
                <button className={activeTab === 'marks' ? 'ums-tab ums-tab--active' : 'ums-tab'} 
                   
                    
                 onClick={() => setActiveTab('marks')}>
                    Marks Management
                </button>
            </div>

            {/* TAB: OVERVIEW */}
            {activeTab === 'overview' && (
                <div style={styles.Section}>
                    <h2 className="ums-title">Faculty Information</h2>
                    {facultyInfo && (
                        <div style={styles.infoGrid}>
                            <div style={styles.infoCard}>
                                <strong>Name:</strong> <p>{facultyInfo.name}</p>
                            </div>
                            <div style={styles.infoCard}>
                                <strong>Employee ID:</strong> <p>{facultyInfo.empId}</p>
                            </div>
                            <div style={styles.infoCard}>
                                <strong>Email:</strong> <p>{facultyInfo.email}</p>
                            </div>
                            <div style={styles.infoCard}>
                                <strong>Department ID:</strong> <p>{facultyInfo.deptId}</p>
                            </div>
                            <div style={styles.infoCard}>
                                <strong>Qualification:</strong> <p>{facultyInfo.qualification || 'N/A'}</p>
                            </div>
                            <div style={styles.infoCard}>
                                <strong>Status:</strong> <p>{facultyInfo.status || 'Active'}</p>
                            </div>
                        </div>
                    )}

                    <h2 style={{ marginTop: '30px' }}>Quick Stats</h2>
                    <div style={styles.infoGrid}>
                        <div style={styles.infoCard}>
                            <strong>Assigned Courses:</strong> <p style={{ fontSize: '24px', color: '#28a745' }}>{assignedCourses.length}</p>
                        </div>
                        <div style={styles.infoCard}>
                            <strong>Total Students:</strong> <p style={{ fontSize: '24px', color: '#007bff' }}>{courseStudents.length}</p>
                        </div>
                        <div style={styles.infoCard}>
                            <strong>Attendance Records:</strong> <p style={{ fontSize: '24px', color: '#ffc107' }}>{courseAttendance.length}</p>
                        </div>
                    </div>
                </div>
            )}

            {/* TAB: MY COURSES */}
            {activeTab === 'courses' && (
                <div style={styles.Section}>
                    <h2>Assigned Courses</h2>
                    {assignedCourses.length > 0 ? (
                        <div className="ums-table-wrap"><table className="ums-table">
                            <thead>
                                <tr>
                                    <th style={styles.th}>Course ID</th>
                                    <th style={styles.th}>Course Code</th>
                                    <th style={styles.th}>Course Name</th>
                                    <th style={styles.th}>Credits</th>
                                    <th style={styles.th}>Capacity</th>
                                    <th style={styles.th}>Current Enrollment</th>
                                    <th style={styles.th}>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                {assignedCourses.map((course) => (
                                    <tr key={course.courseId}>
                                        <td style={styles.td}>{course.courseId}</td>
                                        <td style={styles.td}>{course.courseCode}</td>
                                        <td style={styles.td}>{course.courseName}</td>
                                        <td style={styles.td}>{course.credits}</td>
                                        <td style={styles.td}>{course.maxCapacity}</td>
                                        <td style={styles.td}>{course.currentEnrollment}</td>
                                        <td style={styles.td}>
                                            <button className="ums-btn ums-btn--info" 
                                               
                                                onClick={() => { setSelectedCourse(course.courseId); setActiveTab('students'); }}
                                            >
                                                View Students
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table></div>
                    ) : (
                        <p>No courses assigned.</p>
                    )}
                </div>
            )}

            {/* TAB: COURSE STUDENTS */}
            {activeTab === 'students' && (
                <div style={styles.Section}>
                    <h2>Students in Selected Course</h2>
                    {selectedCourse ? (
                        <p><strong>Course ID: {selectedCourse}</strong></p>
                    ) : (
                        <p style={{ color: 'orange' }}>Please select a course from the "My Courses" tab</p>
                    )}
                    
                    {courseStudents.length > 0 ? (
                        <div className="ums-table-wrap"><table className="ums-table">
                            <thead>
                                <tr>
                                    <th style={styles.th}>Enrollment ID</th>
                                    <th style={styles.th}>Student ID</th>
                                    <th style={styles.th}>Enrollment Date</th>
                                    <th style={styles.th}>CGPA at Enrollment</th>
                                    <th style={styles.th}>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                {courseStudents.map((student) => (
                                    <tr key={student.enrollmentId}>
                                        <td style={styles.td}>{student.enrollmentId}</td>
                                        <td style={styles.td}>{student.studentId}</td>
                                        <td style={styles.td}>{student.enrollmentDate}</td>
                                        <td style={styles.td}>{student.cgpaAtEnrollment?.toFixed(2)}</td>
                                        <td style={styles.td}>
                                            <span style={{ padding: '4px 8px', borderRadius: '4px', backgroundColor: student.status === 'Enrolled' ? '#d4edda' : '#f8d7da', color: student.status === 'Enrolled' ? '#155724' : '#721c24' }}>
                                                {student.status}
                                            </span>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table></div>
                    ) : (
                        <p>No students in this course yet.</p>
                    )}
                </div>
            )}

            {/* TAB: COURSE ATTENDANCE */}
            {activeTab === 'attendance' && (
                <div style={styles.Section}>
                    <h2>Course Attendance Management</h2>
                    
                    {/* Record/Edit Attendance Form */}
                    <div style={{ ...styles.card, backgroundColor: '#e8f5e8', marginBottom: '30px' }}>
                        <h3>{editingAttendanceId ? '✏️ Edit Attendance' : '📝 Mark New Attendance'}</h3>
                        {selectedCourse ? (
                            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>
                                <div>
                                    <label className="ums-label">Student:</label>
                                    <select className="ums-select" 
                                        value={attendanceForm.studentId}
                                        onChange={(e) => { setAttendanceForm({ ...attendanceForm, studentId: e.target.value }); setError(''); }}
                                        style={{ padding: '10px', border: '1px solid #ddd', borderRadius: '4px', width: '100%' }}
                                        disabled={editingAttendanceId !== null}
                                    >
                                        <option value="">-- Select Student --</option>
                                        {courseStudents.map((student) => (
                                            <option key={student.studentId} value={student.studentId}>
                                                {studentNames[student.studentId] || `Student ${student.studentId}`}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                <div>
                                    <label className="ums-label">Date:</label>
                                    <input className="ums-input" 
                                        type="date"
                                        value={attendanceForm.date}
                                        onChange={(e) => { setAttendanceForm({ ...attendanceForm, date: e.target.value }); setError(''); }}
                                        style={{ padding: '10px', border: '1px solid #ddd', borderRadius: '4px', width: '100%' }}
                                    />
                                </div>

                                <div>
                                    <label className="ums-label">Status:</label>
                                    <select className="ums-select" 
                                        value={attendanceForm.status}
                                        onChange={(e) => { setAttendanceForm({ ...attendanceForm, status: e.target.value }); setError(''); }}
                                        style={{ padding: '10px', border: '1px solid #ddd', borderRadius: '4px', width: '100%' }}
                                    >
                                        <option value="Present">Present</option>
                                        <option value="Absent">Absent</option>
                                        <option value="Leave">Leave</option>
                                    </select>
                                </div>

                                <div style={{ display: 'flex', gap: '8px', alignItems: 'flex-end' }}>
                                    <button className="ums-btn ums-btn--success" 
                                        onClick={handleMarkAttendance}
                                       
                                    >
                                        {editingAttendanceId ? '✓ Update' : '✓ Mark'}
                                    </button>
                                    {editingAttendanceId && (
                                        <button className="ums-btn ums-btn--ghost" 
                                            onClick={handleCancelEdit}
                                           
                                        >
                                            Cancel
                                        </button>
                                    )}
                                </div>
                            </div>
                        ) : (
                            <p style={{ color: 'orange', padding: '10px', backgroundColor: '#fff3cd', borderRadius: '4px' }}>
                                ⚠ Please select a course from the "My Courses" tab first
                            </p>
                        )}
                    </div>

                    {/* Attendance Table */}
                    <h3>📋 Attendance Records
                        <button className="ums-btn ums-btn--primary" 
                            onClick={() => fetchCourseDetails(selectedCourse)}
                            style={{ marginLeft: '10px', padding: '6px 12px', backgroundColor: '#17a2b8', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '14px' }}
                        >
                            🔄 Refresh
                        </button>
                    </h3>
                    {selectedCourse && courseAttendance.length > 0 ? (
                        <div style={{ overflowX: 'auto' }}>
                            <div className="ums-table-wrap"><table className="ums-table">
                                <thead>
                                    <tr>
                                        <th style={styles.th}>Student Name</th>
                                        <th style={styles.th}>Date</th>
                                        <th style={styles.th}>Status</th>
                                        <th style={styles.th}>Recorded By</th>
                                        <th style={styles.th}>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {courseAttendance.map((att) => {
                                        const statusColor = att.status === 'Present' ? '#d4edda' : att.status === 'Leave' ? '#cfe2ff' : '#f8d7da';
                                        const statusTextColor = att.status === 'Present' ? '#155724' : att.status === 'Leave' ? '#0c5460' : '#721c24';
                                        
                                        return (
                                            <tr key={att.attendanceId}>
                                                <td style={styles.td}><strong>{studentNames[att.studentId] || `Student ${att.studentId}`}</strong></td>
                                                <td style={styles.td}>{att.date}</td>
                                                <td style={styles.td}>
                                                    <span style={{ padding: '4px 8px', borderRadius: '4px', backgroundColor: statusColor, color: statusTextColor, fontWeight: 'bold' }}>
                                                        {att.status}
                                                    </span>
                                                </td>
                                                <td style={styles.td}>{att.recordedBy}</td>
                                                <td style={styles.td}>
                                                    <button className="ums-btn ums-btn--primary" 
                                                        onClick={() => handleEditAttendance(att)}
                                                        style={{ padding: '6px 12px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '13px' }}
                                                    >
                                                        ✏️ Edit
                                                    </button>
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table></div>
                        </div>
                    ) : selectedCourse ? (
                        <p style={{ color: '#666', padding: '10px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
                            No attendance records yet for this course.
                        </p>
                    ) : (
                        <p style={{ color: 'orange' }}>Please select a course from the "My Courses" tab</p>
                    )}
                </div>
            )}

            {/* TAB: MARKS MANAGEMENT */}
            {activeTab === 'marks' && (
                <div style={styles.Section}>
                    <h2>Marks Management</h2>
                    
                    {/* Record New Marks Form */}
                    <div style={{ ...styles.card, backgroundColor: '#f0f8f0', marginBottom: '30px' }}>
                        <h3>📝 Record New Marks</h3>
                        <p style={{ fontSize: '13px', color: '#666', marginBottom: '15px' }}>
                            Internal Marks (0-30) + External Marks (0-70) = Total (0-100)
                        </p>
                        {selectedCourse ? (
                            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>
                                <div>
                                    <label className="ums-label">Student:</label>
                                    <select className="ums-select" 
                                        value={markForm.studentId}
                                        onChange={(e) => setMarkForm({ ...markForm, studentId: e.target.value })}
                                        style={{ padding: '10px', border: '1px solid #ddd', borderRadius: '4px', width: '100%' }}
                                    >
                                        <option value="">-- Select Student --</option>
                                        {courseStudents.map((student) => (
                                            <option key={student.studentId} value={student.studentId}>
                                                {studentNames[student.studentId] || `Student ${student.studentId}`}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                                
                                <div>
                                    <label className="ums-label">Internal Marks (0-30):</label>
                                    <input className="ums-input" 
                                        type="number" 
                                        min="0"
                                        max="30"
                                        step="0.5"
                                        placeholder="0-30" 
                                        value={markForm.internalMarks}
                                        onChange={(e) => { setMarkForm({ ...markForm, internalMarks: e.target.value }); setError(''); }}
                                        style={{ padding: '10px', border: '1px solid #ddd', borderRadius: '4px', width: '100%' }}
                                    />
                                </div>

                                <div>
                                    <label className="ums-label">External Marks (0-70):</label>
                                    <input className="ums-input" 
                                        type="number" 
                                        min="0"
                                        max="70"
                                        step="0.5"
                                        placeholder="0-70" 
                                        value={markForm.externalMarks}
                                        onChange={(e) => { setMarkForm({ ...markForm, externalMarks: e.target.value }); setError(''); }}
                                        style={{ padding: '10px', border: '1px solid #ddd', borderRadius: '4px', width: '100%' }}
                                    />
                                </div>

                                {markForm.internalMarks && markForm.externalMarks && (
                                    <div>
                                        <label className="ums-label">Total Calculated:</label>
                                        <input className="ums-input" 
                                            type="number" 
                                            value={(parseFloat(markForm.internalMarks || 0) + parseFloat(markForm.externalMarks || 0)).toFixed(1)}
                                            disabled
                                           
                                        />
                                    </div>
                                )}
                                
                                <button className="ums-btn ums-btn--primary" 
                                    onClick={handleRecordMarks}
                                   
                                >
                                    ✓ Record Marks
                                </button>
                            </div>
                        ) : (
                            <p style={{ color: 'orange', padding: '10px', backgroundColor: '#fff3cd', borderRadius: '4px' }}>
                                ⚠ Please select a course from the "My Courses" tab first
                            </p>
                        )}
                    </div>

                    {/* Marks Table */}
                    <h3>📊 Course Marks Records  
                        <button className="ums-btn ums-btn--primary" 
                            onClick={() => fetchCourseDetails(selectedCourse)}
                            style={{ marginLeft: '10px', padding: '6px 12px', backgroundColor: '#17a2b8', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '14px' }}
                        >
                            🔄 Refresh
                        </button>
                    </h3>
                    {courseMarks && courseMarks.length > 0 ? (
                        <div style={{ overflowX: 'auto' }}>
                            <div className="ums-table-wrap"><table className="ums-table">
                                <thead>
                                    <tr>
                                        <th style={styles.th}>Student ID</th>
                                        <th style={styles.th}>Student Name</th>
                                        <th style={styles.th} title="Internal Assessment (Max 30)">Internal⁕</th>
                                        <th style={styles.th} title="External Exam (Max 70)">External⁕</th>
                                        <th style={styles.th} title="Total (Max 100)">Total⁕</th>
                                        <th style={styles.th}>Percentage</th>
                                        <th style={styles.th}>Grade</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {courseMarks.map((mark) => {
                                        const percentage = ((mark.totalMarks / 100) * 100).toFixed(2);
                                        
                                        // Grade color coding
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
                                                <td style={styles.td}><strong>{mark.studentId}</strong></td>
                                                <td style={styles.td}>{studentNames[mark.studentId] || `Student ${mark.studentId}`}</td>
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
                            </table></div>
                            <p style={{ fontSize: '12px', color: '#666', marginTop: '10px' }}>
                                ⁕ Grade: A ≥ 90 | B ≥ 80 | C ≥ 70 | D ≥ 60 | F &lt; 60
                            </p>
                        </div>
                    ) : (
                        <p style={{ color: '#666', padding: '10px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
                            No marks recorded yet for this course.
                        </p>
                    )}
                </div>
            )}
        </div>
    );
}