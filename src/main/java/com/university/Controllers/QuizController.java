package com.university.Controllers;

import com.university.services.QuizService;
import com.university.models.Quiz;
import com.university.models.Question;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.university.dto.QuizSubmissionRequest;
 import com.university.models.QuizAttempt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin(origins = "*")
public class QuizController {

    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    // ================= FACULTY ENDPOINTS =================

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createQuiz(@RequestBody Quiz quiz) {
        Map<String, Object> response = new HashMap<>();
        boolean success = quizService.createQuiz(quiz);
        
        if (success) {
            response.put("status", "success");
            response.put("message", "Quiz created successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Failed to create quiz");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/add-question")
    public ResponseEntity<Map<String, Object>> addQuestion(@RequestBody Question question) {
        Map<String, Object> response = new HashMap<>();
        boolean success = quizService.addQuestion(question);
        
        if (success) {
            response.put("status", "success");
            response.put("message", "Question added successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Failed to add question");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ================= STUDENT ENDPOINTS =================

    @GetMapping("/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getQuizzesByCourse(@PathVariable int courseId) {
        Map<String, Object> response = new HashMap<>();
        List<Quiz> quizzes = quizService.getQuizzesForCourse(courseId);
        
        response.put("status", "success");
        response.put("data", quizzes);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{quizId}/questions")
    public ResponseEntity<Map<String, Object>> getQuizQuestions(@PathVariable int quizId) {
        Map<String, Object> response = new HashMap<>();
        List<Question> questions = quizService.getQuestionsForQuiz(quizId);
        
        // Security: Remove correct answers before sending to frontend!
        for (Question q : questions) {
            q.setCorrectOption(null); 
        }

        response.put("status", "success");
        response.put("data", questions);
        return ResponseEntity.ok(response);
    }
   @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitQuiz(@RequestBody com.university.dto.QuizSubmissionRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Anti-cheating check: Ensure they haven't already taken it
            if (quizService.hasStudentAttempted(request.getStudentId(), request.getQuizId())) {
                response.put("status", "error");
                response.put("message", "You have already attempted this quiz.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // 1. Grade the test and save it
            com.university.models.QuizAttempt result = quizService.submitQuizAndCalculateScore(
                request.getStudentId(), 
                request.getQuizId(), 
                request.getAnswers()
            );

            if (result != null) {
                // 2. Fetch the full questions (WITH correct answers) to send back for the review page
                List<Question> fullQuestions = quizService.getQuestionsForQuiz(request.getQuizId());
                
                // 3. Calculate exact number of correct answers to send back (e.g., 4 out of 5)
                int correctCount = (int) Math.round((result.getScore() / 100.0) * result.getTotalQuestions());

                response.put("status", "success");
                response.put("message", "Quiz submitted successfully");
                response.put("score", result.getScore());
                response.put("correctCount", correctCount);
                response.put("totalQuestions", result.getTotalQuestions());
                response.put("reviewData", fullQuestions); // Send answers back to React
                
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to save quiz results.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An error occurred during submission.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}