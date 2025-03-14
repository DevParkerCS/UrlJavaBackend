package com.parker.url.url_shortener.AuthenticationAPI;

import java.util.regex.Pattern;
import java.time.Duration;
import java.util.*;

import org.apache.catalina.manager.util.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class UserEndpoints {
    @Autowired
    private UserInfoRepository userRepo;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> postMethodName(@RequestBody String email, @RequestBody String password, @RequestBody String firstName, 
    @RequestBody String lastName, HttpServletResponse httpResponse) {
        
        // Ensure email and password match regex requirements
        if(!isEmailValid(email) || !isPasswordValid(password)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }

        HashMap<String, String> response = new HashMap<>();
        Optional<UserInfo> info = userRepo.findByEmail(email);

        // Check if user has already made an account under the email
        if(info.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        createLoginCookie(httpResponse);

        // Create new user for SQL table
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(email);
        userInfo.setFirstName(firstName);
        userInfo.setLastName(lastName);
        String encodedPassword = encoder.encode(password);
        userInfo.setPassword(encodedPassword);
        // Save user into SQL table
        userRepo.save(userInfo);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> getMethodName(@RequestBody String email, @RequestBody String password, HttpServletResponse response) {
        Optional<UserInfo> info = userRepo.findByEmail(email);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Check that a user does indeed exist under the email
        if(info.isPresent()) {
            // Check that the user has entered the correct password for the email
            if(encoder.matches(password, info.get().getPassword())) {
                HashMap<String, String> userMap = new HashMap<>();
                userMap.put("firstName", info.get().getFirstName());
                userMap.put("lastName", info.get().getLastName());
                userMap.put("email", info.get().getEmail());
                createLoginCookie(response);
                return ResponseEntity.ok(userMap);
            }
            return ResponseEntity.ok(null);
        }else {
            return ResponseEntity.ok(null);
        }
    }

    private void createLoginCookie(HttpServletResponse response) {
        // Create new login cookie with unique session id
        Cookie loginCookie = new Cookie("session_id", UUID.randomUUID().toString());

        // Set cookie attributes
        loginCookie.setMaxAge(3600);
        loginCookie.setSecure(false);
        loginCookie.setHttpOnly(true);
        loginCookie.setAttribute("SameSite", "Strict");
        
        // Add cookie to the response
        response.addCookie(loginCookie);
    }
    
    // Check that email matches regex pattern
    private boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);

        if (email == null) {
            return false;
        }

        return pattern.matcher(email).matches();
    }

    // Check that password matches regex pattern
    private boolean isPasswordValid(String password) {
        // Ensure 1 capital, 1 special character, 1 number, and 6 characters long
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{6,}$";
        Pattern pattern = Pattern.compile(passwordRegex);

        if (password == null) {
            return false;
        }

        return pattern.matcher(password).matches();
    }
}
