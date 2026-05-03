package com.university.services;

import com.university.dao.QuizDAO;
import com.university.dao.QuestionDAO;
import com.university.models.Quiz;
import com.university.models.QuizAttempt;
import com.university.dao.QuizAttemptDAO;
import java.util.Map ;
import com.university.models.Question;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;



@Service
public class QuizService {

    private final QuizDAO quizDAO;
    private final QuestionDAO questionDAO;

    @Autowired
    public QuizService(QuizDAO quizDAO, QuestionDAO questionDAO) {
        this.quizDAO = quizDAO;
        this.questionDAO = questionDAO;
    }

    public String validateQuiz(Quiz quiz) {
        if (quiz.getTitle() == null || quiz.getTitle().trim().isEmpty()) {
            return "Quiz title is required.";
        }
        if (quiz.getCourseId() <= 0 || quiz.getFacultyId() <= 0) {
            return "Valid Course ID and Faculty ID are required.";
        }
        if (quiz.getDurationMinutes() < 1) {
            return "Duration must be at least 1 minute.";
        }
        return "VALID";
    }
     @Autowired
    private QuizAttemptDAO quizAttemptDAO;

    public boolean hasStudentAttempted(int studentId, int quizId) {
        return quizAttemptDAO.hasStudentAttemptedQuiz(studentId, quizId);
    }

    public QuizAttempt submitQuizAndCalculateScore(int studentId, int quizId, Map<Integer, String> studentAnswers) {
        // 1. Fetch the real questions (with the correct answers intact)
        List<Question> realQuestions = questionDAO.getQuestionsByQuizId(quizId);
        
        int correctCount = 0;
        int totalQuestions = realQuestions.size();

        // 2. Grade the exam
        for (Question q : realQuestions) {
            String selectedOption = studentAnswers.get(q.getQuestionId());
            if (selectedOption != null && selectedOption.equalsIgnoreCase(q.getCorrectOption())) {
                correctCount++;
            }
        }

        // 3. Calculate percentage
        double score = totalQuestions > 0 ? ((double) correctCount / totalQuestions) * 100 : 0.0;

        // 4. Create and save the attempt
        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudentId(studentId);
        attempt.setQuizId(quizId);
        attempt.setScore(score);
        attempt.setTotalQuestions(totalQuestions);

        boolean saved = quizAttemptDAO.saveAttempt(attempt);
        
        return saved ? attempt : null;
    }

    public boolean createQuiz(Quiz quiz) {
        String validationMsg = validateQuiz(quiz);
        if (!validationMsg.equals("VALID")) {
            System.err.println(validationMsg);
            return false;
        }
        return quizDAO.createQuiz(quiz);
    }

    public boolean addQuestion(Question question) {
        if (question.getQuizId() <= 0) return false;
        if (question.getCorrectOption() == null || !question.getCorrectOption().matches("[A-D]")) {
            return false;
        }
        return questionDAO.addQuestion(question);
    }

    public List<Quiz> getQuizzesForCourse(int courseId) {
        return quizDAO.getQuizzesByCourseId(courseId);
    }

    public List<Question> getQuestionsForQuiz(int quizId) {
        // Business Rule: Ensure students don't see the correct_option when fetching!
        // We will handle hiding the answer in the Controller or React frontend.
        return questionDAO.getQuestionsByQuizId(quizId);
    }
}