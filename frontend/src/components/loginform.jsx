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
        e.preventDefault(); // Stops the page from refreshing
        setMessage('Logging in...');
        setIsLoading(true);
        
        try {
            // 3. Send the exact JSON shape your Spring Boot DTO expects!
            const response = await axios.post('http://localhost:8080/api/auth/login', {
                identifier: identifier,
                password: password,
                role: role
            });

            console.log("Full Response:", response);
            console.log("Response Data:", response.data);
            console.log("User Data:", response.data.user);
            console.log("Role being used:", role);

            // 4. If Spring Boot says OK (200)
            setMessage("Success! " + response.data.message);
            setIsError(false);

            // Store auth data in state AND localStorage
            const authPayload = {
                user: response.data.user,
                role: role,
                message: response.data.message
            };

            console.log("Auth Payload being stored:", authPayload);
            
            setAuthData(authPayload);
            localStorage.setItem('authData', JSON.stringify(authPayload));

            // Redirect based on role - using strict equality
            console.log(`Redirecting to: /${role.toLowerCase()}`);
            
            if (role === 'ADMIN') {
                setTimeout(() => navigate('/admin'), 500);
            } else if (role === 'FACULTY') {
                setTimeout(() => navigate('/faculty'), 500);
            } else if (role === 'STUDENT') {
                setTimeout(() => navigate('/student'), 500);
            } else {
                console.error("Invalid role:", role);
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
                setMessage("Cannot connect to server. Is Spring Boot running on http://localhost:8080?");
            }
        } finally {
            setIsLoading(false);
        }
    };

    // 6. The actual HTML/UI
    return (
        <div style={{ maxWidth: '400px', margin: '50px auto', fontFamily: 'sans-serif' }}>
            <h2>University Login</h2>
            
            <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                
                <input 
                    type="text" 
                    placeholder="Username or Roll Number" 
                    value={identifier}
                    onChange={(e) => setIdentifier(e.target.value)}
                    required
                    disabled={isLoading}
                    style={{ padding: '10px' }}
                />

                <input 
                    type="password" 
                    placeholder="Password" 
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    disabled={isLoading}
                    style={{ padding: '10px' }}
                />

                <select 
                    value={role} 
                    onChange={(e) => setRole(e.target.value)}
                    disabled={isLoading}
                    style={{ padding: '10px' }}
                >
                    <option value="STUDENT">Student</option>
                    <option value="FACULTY">Faculty</option>
                    <option value="ADMIN">Admin</option>
                </select>

                <button 
                    type="submit" 
                    disabled={isLoading}
                    style={{ 
                        padding: '10px', 
                        backgroundColor: isLoading ? '#cccccc' : '#0056b3', 
                        color: 'white', 
                        border: 'none', 
                        cursor: isLoading ? 'not-allowed' : 'pointer'
                    }}
                >
                    {isLoading ? 'Logging in...' : 'Login'}
                </button>

            </form>

            {/* Display our success or error message here */}
            {message && (
                <div style={{ marginTop: '20px', padding: '10px', backgroundColor: isError ? '#ffebee' : '#e8f5e9', color: isError ? 'red' : 'green' }}>
                    {message}
                </div>
            )}
        </div>
    );
}