package com.university.Controllers;

import com.university.services.AuthService;
import com.university.dto.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Temporarily allow React to talk to this controller
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        
        System.out.println("DEBUG AuthController: Login attempt");
        System.out.println("  Identifier: " + request.getIdentifier());
        System.out.println("  Role: " + request.getRole());
        
        // 1. Hand the data to your Java Core
        Object authenticatedUser = authService.authenticate(
                request.getIdentifier(), 
                request.getPassword(), 
                request.getRole()
        );

        System.out.println("DEBUG AuthController: Authentication result: " + (authenticatedUser != null ? "SUCCESS" : "FAILED"));

        // 2. Prepare the JSON response package
        Map<String, Object> response = new HashMap<>() ;

        if (authenticatedUser != null) {
            // Success!
            response.put("status", "success");
            response.put("message", "Login successful");
            response.put("user", authenticatedUser); 
            return ResponseEntity.ok(response); // Sends an HTTP 200 OK
        } else {
            // Failure!
            response.put("status", "error");
            response.put("message", "Invalid credentials or inactive account");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // Sends an HTTP 401
        }
    }
}