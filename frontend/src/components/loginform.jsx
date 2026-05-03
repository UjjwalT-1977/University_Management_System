import { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

export default function LoginForm({ setAuthData }) {
    // 1. Set up memory (state) for our form fields
    const navigate = useNavigate();
    const [identifier, setIdentifier] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('STUDENT');
    
    // Memory for showing success or error messages
    const [message, setMessage] = useState('');
    const [isError, setIsError] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

    // 2. What happens when the user clicks submit?
    const handleLogin = async (e) => {
        e.preventDefault();
        setMessage('Authenticating...');
        setIsLoading(true);
        
        try {
            // 3. Send the exact JSON shape your Spring Boot DTO expects
            const response = await axios.post('http://localhost:8080/api/auth/login', {
                identifier: identifier,
                password: password,
                role: role
            });

            console.log("Full Response:", response);
            console.log("Response Data:", response.data);
            
            // 4. If Spring Boot says OK (200)
            setMessage("Success! Redirecting...");
            setIsError(false);

            // Store auth data in state AND localStorage
            const authPayload = {
                user: response.data.user,
                role: role,
                message: response.data.message
            };
            
            setAuthData(authPayload);
            localStorage.setItem('authData', JSON.stringify(authPayload));

            // Redirect based on role
            if (role === 'ADMIN') {
                setTimeout(() => navigate('/admin'), 500);
            } else if (role === 'FACULTY') {
                setTimeout(() => navigate('/faculty'), 500);
            } else if (role === 'STUDENT') {
                setTimeout(() => navigate('/student'), 500);
            } else {
                setIsError(true);
                setMessage("Invalid role selected");
            }

        } catch (error) {
            // 5. If Spring Boot throws an error
            console.error("Login Error:", error);
            setIsError(true);
            setIsLoading(false);
            
            if (error.response && error.response.data) {
                setMessage(error.response.data.message);
            } else if (error.message) {
                setMessage("Error: " + error.message);
            } else {
                setMessage("Cannot connect to server. Is Spring Boot running?");
            }
        } finally {
            if (isError) setIsLoading(false);
        }
    };

    // --- UI STYLES ---
    const styles = {
        page: {
            minHeight: '100vh',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            backgroundColor: '#f4f7f6', // Light gray-blue background
            fontFamily: "'Segoe UI', Roboto, Helvetica, Arial, sans-serif"
        },
        card: {
            backgroundColor: '#ffffff',
            padding: '40px 40px',
            borderRadius: '12px',
            boxShadow: '0 10px 25px rgba(0, 0, 0, 0.05)',
            width: '100%',
            maxWidth: '420px',
            boxSizing: 'border-box'
        },
        header: {
            textAlign: 'center',
            marginBottom: '30px'
        },
        icon: {
            fontSize: '40px',
            marginBottom: '10px',
            display: 'block'
        },
        title: {
            margin: '0',
            color: '#1a365d', // Dark academic blue
            fontSize: '24px',
            fontWeight: 'bold'
        },
        subtitle: {
            margin: '8px 0 0 0',
            color: '#718096',
            fontSize: '14px'
        },
        alertBox: {
            padding: '12px 16px',
            borderRadius: '6px',
            marginBottom: '20px',
            fontSize: '14px',
            textAlign: 'center',
            fontWeight: '500'
        },
        alertSuccess: {
            backgroundColor: '#e6fffa',
            color: '#234e52',
            border: '1px solid #b2f5ea'
        },
        alertError: {
            backgroundColor: '#fff5f5',
            color: '#c53030',
            border: '1px solid #fed7d7'
        },
        formGroup: {
            marginBottom: '20px'
        },
        label: {
            display: 'block',
            marginBottom: '8px',
            color: '#4a5568',
            fontSize: '14px',
            fontWeight: '600'
        },
        input: {
            width: '100%',
            padding: '12px 16px',
            borderRadius: '6px',
            border: '1px solid #e2e8f0',
            fontSize: '15px',
            color: '#2d3748',
            backgroundColor: '#f8fafc',
            boxSizing: 'border-box',
            outline: 'none',
            transition: 'border-color 0.2s, background-color 0.2s'
        },
        button: {
            width: '100%',
            padding: '14px',
            backgroundColor: '#2b6cb0', // Primary button blue
            color: 'white',
            border: 'none',
            borderRadius: '6px',
            fontSize: '16px',
            fontWeight: 'bold',
            cursor: isLoading ? 'not-allowed' : 'pointer',
            opacity: isLoading ? 0.7 : 1,
            marginTop: '10px',
            transition: 'background-color 0.2s'
        },
        footerText: {
            textAlign: 'center',
            fontSize: '12px',
            marginTop: '24px',
            color: '#a0aec0'
        }
    };

    // 6. The actual HTML/UI
    return (
        <div style={styles.page}>
            <div style={styles.card}>
                <div style={styles.header}>
                    <span style={styles.icon}>🎓</span>
                    <h1 style={styles.title}>University Portal</h1>
                    <p style={styles.subtitle}>Sign in to access your dashboard</p>
                </div>

                {message && (
                    <div style={{...styles.alertBox, ...(isError ? styles.alertError : styles.alertSuccess)}}>
                        {message}
                    </div>
                )}

                <form onSubmit={handleLogin}>
                    <div style={styles.formGroup}>
                        <label style={styles.label}>Identifier (Roll No / ID)</label>
                        <input 
                            style={styles.input}
                            type="text" 
                            placeholder="Enter your identifier" 
                            value={identifier}
                            onChange={(e) => setIdentifier(e.target.value)}
                            required
                            disabled={isLoading}
                        />
                    </div>

                    <div style={styles.formGroup}>
                        <label style={styles.label}>Password</label>
                        <input 
                            style={styles.input}
                            type="password" 
                            placeholder="Enter your password" 
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            disabled={isLoading}
                        />
                    </div>

                    <div style={styles.formGroup}>
                        <label style={styles.label}>Account Role</label>
                        <select 
                            style={styles.input}
                            value={role} 
                            onChange={(e) => setRole(e.target.value)}
                            disabled={isLoading}
                        >
                            <option value="STUDENT">Student</option>
                            <option value="FACULTY">Faculty</option>
                            <option value="ADMIN">Administrator</option>
                        </select>
                    </div>

                    <button 
                        style={styles.button}
                        type="submit" 
                        disabled={isLoading}
                        // Added simple inline hover effect simulation
                        onMouseOver={(e) => !isLoading && (e.target.style.backgroundColor = '#2c5282')}
                        onMouseOut={(e) => !isLoading && (e.target.style.backgroundColor = '#2b6cb0')}
                    >
                        {isLoading ? 'Authenticating...' : 'Sign In'}
                    </button>
                </form>

                <p style={styles.footerText}>
                    Secure access for students, faculty, and administrators.
                </p>
            </div>
        </div>
    );
}