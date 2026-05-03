import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import '../App.css';

export default function TakeQuiz({ authData }) {
    const { quizId } = useParams();
    const navigate = useNavigate();
    const location = useLocation();
    
    const durationMinutes = location.state?.duration || 30;
    const quizTitle = location.state?.title || "University Quiz";

    const [questions, setQuestions] = useState([]);
    const [answers, setAnswers] = useState({}); 
    const [timeLeft, setTimeLeft] = useState(durationMinutes * 60); 
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');
    const [scoreResult, setScoreResult] = useState(null);

    // 1. Fetch questions on load
    useEffect(() => {
        const fetchQuestions = async () => {
            try {
                const res = await axios.get(`http://localhost:8080/api/quiz/${quizId}/questions`);
                setQuestions(res.data.data || []);
            } catch (err) {
                setError('Failed to load quiz questions. ' + (err.response?.data?.message || err.message));
            } finally {
                setLoading(false);
            }
        };
        fetchQuestions();
    }, [quizId]);

    // 2. Countdown Timer Logic
    useEffect(() => {
        if (loading || submitting || scoreResult !== null || error) return;

        if (timeLeft <= 0) {
            handleAutoSubmit();
            return;
        }

        const timerId = setInterval(() => {
            setTimeLeft(prev => prev - 1);
        }, 1000);

        return () => clearInterval(timerId);
    }, [timeLeft, loading, submitting, scoreResult, error]);

    const handleOptionSelect = (questionId, optionKey) => {
        setAnswers({
            ...answers,
            [questionId]: optionKey
        });
    };

    const submitQuizToServer = async () => {
        try {
            setSubmitting(true);
            setError('');
            
            const validStudentId = authData?.user?.id || authData?.id || 1;
            
            const payload = {
                studentId: parseInt(validStudentId),
                quizId: parseInt(quizId),
                answers: answers
            };

            const response = await axios.post('http://localhost:8080/api/quiz/submit', payload);

            if (response.data.status === 'success') {
                // Store all the rich data coming back from the new backend update!
                setScoreResult({
                    percentage: response.data.score,
                    correct: response.data.correctCount,
                    total: response.data.totalQuestions,
                    reviewData: response.data.reviewData 
                });
            } else {
                setError(response.data.message || "Submission failed.");
            }
        } catch (err) {
            console.error(err);
            setError(err.response?.data?.message || "An error occurred during submission.");
        } finally {
            setSubmitting(false);
        }
    };

    const handleManualSubmit = (e) => {
        e.preventDefault();
        if (window.confirm("Are you sure you want to submit your quiz? You cannot change your answers after submitting.")) {
            submitQuizToServer();
        }
    };

    const handleAutoSubmit = () => {
        alert("Time is up! Submitting your quiz automatically.");
        submitQuizToServer();
    };

    const formatTime = (seconds) => {
        const m = Math.floor(seconds / 60);
        const s = seconds % 60;
        return `${m}:${s < 10 ? '0' : ''}${s}`;
    };

    // --- UI STATES ---

    if (loading) return <div className="ums-page flex-center"><h2>Loading Quiz Environment...</h2></div>;

    if (error) return (
        <div className="ums-page flex-center">
            <div className="ums-panel text-center">
                <h2 style={{ color: 'red' }}>Error</h2>
                <p>{error}</p>
                <button className="ums-btn ums-btn--primary mt-3" onClick={() => navigate('/student')}>Return to Dashboard</button>
            </div>
        </div>
    );

    // ================= NEW POST-SUBMISSION REVIEW UI =================
    if (scoreResult !== null) return (
        <div style={{ backgroundColor: '#f5f7fa', minHeight: '100vh', padding: '40px 20px' }}>
            <div style={{ maxWidth: '800px', margin: '0 auto' }}>
                
                {/* Score Summary Box */}
                <div className="ums-panel text-center mb-4" style={{ borderTop: '5px solid #28a745' }}>
                    <h1 style={{ fontSize: '48px', margin: '10px 0' }}>🎉</h1>
                    <h2>Quiz Submitted Successfully!</h2>
                    
                    <div style={{ display: 'flex', justifyContent: 'center', gap: '20px', marginTop: '20px' }}>
                        <div style={{ backgroundColor: '#e8f5e9', padding: '20px', borderRadius: '8px', minWidth: '150px' }}>
                            <p style={{ fontSize: '16px', margin: 0, color: '#28a745', fontWeight: 'bold' }}>Correct Answers</p>
                            <h1 style={{ color: '#155724', fontSize: '36px', margin: '5px 0' }}>{scoreResult.correct} / {scoreResult.total}</h1>
                        </div>
                        <div style={{ backgroundColor: '#e7f3ff', padding: '20px', borderRadius: '8px', minWidth: '150px' }}>
                            <p style={{ fontSize: '16px', margin: 0, color: '#007bff', fontWeight: 'bold' }}>Overall Score</p>
                            <h1 style={{ color: '#004085', fontSize: '36px', margin: '5px 0' }}>{scoreResult.percentage.toFixed(1)}%</h1>
                        </div>
                    </div>
                    <button className="ums-btn ums-btn--primary mt-4" onClick={() => navigate('/student')}>Return to Dashboard</button>
                </div>

                {/* Detailed Review Section */}
                <h3 className="mb-3">Review Your Answers</h3>
                {scoreResult.reviewData.map((q, index) => {
                    const studentChoice = answers[q.questionId];
                    const isCorrect = studentChoice === q.correctOption;

                    return (
                        <div key={q.questionId} className="ums-panel mb-4" style={{ textAlign: 'left', borderLeft: isCorrect ? '5px solid #28a745' : '5px solid #dc3545' }}>
                            <h4 style={{ marginBottom: '15px' }}>
                                {index + 1}. {q.questionText}
                                <span style={{ float: 'right', fontSize: '14px', padding: '4px 8px', borderRadius: '4px', backgroundColor: isCorrect ? '#d4edda' : '#f8d7da', color: isCorrect ? '#155724' : '#721c24' }}>
                                    {isCorrect ? '✓ Correct' : '✗ Incorrect'}
                                </span>
                            </h4>
                            
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', marginLeft: '10px' }}>
                                {['A', 'B', 'C', 'D'].map((opt) => {
                                    const isThisTheCorrectAnswer = opt === q.correctOption;
                                    const isThisWhatStudentPicked = opt === studentChoice;
                                    
                                    // Default styling
                                    let bgColor = '#f8f9fa';
                                    let borderColor = 'transparent';
                                    let textColor = '#333';

                                    // Highlight logic
                                    if (isThisTheCorrectAnswer) {
                                        bgColor = '#d4edda'; // Light green
                                        borderColor = '#28a745';
                                        textColor = '#155724';
                                    } else if (isThisWhatStudentPicked && !isThisTheCorrectAnswer) {
                                        bgColor = '#f8d7da'; // Light red
                                        borderColor = '#dc3545';
                                        textColor = '#721c24';
                                    }

                                    return (
                                        <div key={opt} style={{ 
                                            padding: '10px 15px', backgroundColor: bgColor, border: `2px solid ${borderColor}`,
                                            borderRadius: '6px', color: textColor, display: 'flex', alignItems: 'center', gap: '10px'
                                        }}>
                                            <span style={{ fontWeight: 'bold' }}>{opt}.</span>
                                            <span>{opt === 'A' ? q.optionA : opt === 'B' ? q.optionB : opt === 'C' ? q.optionC : q.optionD}</span>
                                            
                                            {isThisTheCorrectAnswer && <span style={{ marginLeft: 'auto', fontWeight: 'bold' }}>✓ Correct Answer</span>}
                                            {isThisWhatStudentPicked && !isThisTheCorrectAnswer && <span style={{ marginLeft: 'auto', fontWeight: 'bold' }}>✗ Your Choice</span>}
                                        </div>
                                    );
                                })}
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );

    // ================= MAIN QUIZ UI (TEST TAKING MODE) =================
    return (
        <div style={{ backgroundColor: '#f5f7fa', minHeight: '100vh', padding: '20px' }}>
            <div style={{ 
                position: 'sticky', top: '10px', zIndex: 100, backgroundColor: 'white', padding: '15px 30px', 
                borderRadius: '8px', boxShadow: '0 4px 12px rgba(0,0,0,0.1)', display: 'flex', justifyContent: 'space-between', 
                alignItems: 'center', marginBottom: '30px'
            }}>
                <h2 style={{ margin: 0 }}>{quizTitle}</h2>
                <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
                    <div style={{ fontSize: '24px', fontWeight: 'bold', color: timeLeft < 60 ? '#dc3545' : '#28a745', fontFamily: 'monospace' }}>
                        ⏱ {formatTime(timeLeft)}
                    </div>
                    <button className="ums-btn ums-btn--success" onClick={handleManualSubmit} disabled={submitting}>
                        {submitting ? 'Submitting...' : 'Submit Final Answers'}
                    </button>
                </div>
            </div>

            <div style={{ maxWidth: '800px', margin: '0 auto' }}>
                {questions.length === 0 ? (
                    <div className="ums-alert ums-alert--warning">No questions found for this quiz.</div>
                ) : (
                    questions.map((q, index) => (
                        <div key={q.questionId} className="ums-panel mb-4" style={{ textAlign: 'left' }}>
                            <h4 style={{ marginBottom: '20px', lineHeight: '1.5' }}>
                                <span style={{ color: '#007bff', marginRight: '10px' }}>{index + 1}.</span> {q.questionText}
                            </h4>
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginLeft: '25px' }}>
                                {['A', 'B', 'C', 'D'].map((opt) => (
                                    <label key={opt} style={{ 
                                        padding: '12px 15px', 
                                        backgroundColor: answers[q.questionId] === opt ? '#e7f3ff' : '#f8f9fa', 
                                        border: answers[q.questionId] === opt ? '2px solid #007bff' : '2px solid transparent',
                                        borderRadius: '6px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '15px',
                                        transition: 'all 0.2s'
                                    }}>
                                        <input 
                                            type="radio" name={`question-${q.questionId}`} value={opt} checked={answers[q.questionId] === opt}
                                            onChange={() => handleOptionSelect(q.questionId, opt)} style={{ transform: 'scale(1.2)' }}
                                        />
                                        <span style={{ fontWeight: 'bold', color: '#555' }}>{opt}.</span>
                                        <span style={{ fontSize: '16px' }}>{opt === 'A' ? q.optionA : opt === 'B' ? q.optionB : opt === 'C' ? q.optionC : q.optionD}</span>
                                    </label>
                                ))}
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}