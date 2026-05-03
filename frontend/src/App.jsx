import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useState } from 'react';
import LoginForm from './components/loginform';
import AdminDashboard from './components/AdminDashboard';
import FacultyDashboard from './components/FacultyDashboard';
import StudentDashboard from './components/StudentDashboard';
import TakeQuiz from './components/TakeQuiz';

function App() {
  // Store authentication state globally
  const [authData, setAuthData] = useState(() => {
    const saved = localStorage.getItem('authData');
    return saved ? JSON.parse(saved) : null;
  });

  // Protected Route Component
  const ProtectedRoute = ({ element, requiredRole }) => {
    if (!authData) {
      return <Navigate to="/login" replace />;
    }
    if (authData.role !== requiredRole) {
      return <Navigate to="/login" replace />;
    }
    return element;
  };

  return (
    <BrowserRouter>
      <Routes>
        {/* If they just go to localhost:5173, immediately send them to login */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        
        {/* The actual routes */}
        <Route path="/login" element={<LoginForm setAuthData={setAuthData} />} />
        <Route 
          path="/admin" 
          element={
            <ProtectedRoute 
              element={<AdminDashboard authData={authData} />} 
              requiredRole="ADMIN" 
            />
          } 
        />
        <Route 
          path="/faculty" 
          element={
            <ProtectedRoute 
              element={<FacultyDashboard authData={authData} />} 
              requiredRole="FACULTY" 
            />
          } 
        />
        <Route 
          path="/student" 
          element={
            <ProtectedRoute 
              element={<StudentDashboard authData={authData} />} 
              requiredRole="STUDENT" 
            />
          } 
        />
         <Route 
          path="/take-quiz/:quizId" 
          element={
            <ProtectedRoute 
              element={<TakeQuiz authData={authData} />} 
              requiredRole="STUDENT" 
            />
          } 
        />
      </Routes>
     
    </BrowserRouter>
  );
}

export default App;