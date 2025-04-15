package com.parker.url.url_shortener.AuthenticationAPI;

import java.util.regex.Pattern;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import com.parker.url.url_shortener.UserSessions.UserSessions;
import com.parker.url.url_shortener.UserSessions.UserSessionsRepository;
import com.parker.url.url_shortener.Utils.CookieUtils;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class UserEndpoints {
    @Autowired
    private UserInfoRepository userRepo;
    @Autowired
    UserSessionsRepository userSessionsRepo;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> postSignup(@RequestBody Map<String, String> signupInfo,
            HttpServletResponse httpResponse) {

        String username = signupInfo.get("username");
        String password = signupInfo.get("password");
        // Ensure email and password match regex requirements
        if (!isPasswordValid(password)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }

        HashMap<String, String> response = new HashMap<>();
        Optional<UserInfo> info = userRepo.findByUsername(username);

        // Check if user has already made an account under the email
        if (info.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Create new user for SQL table
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        // Adjust this as eventually first and last are optional
        userInfo.setFirstName("First");
        userInfo.setLastName("Last");

        String encodedPassword = encoder.encode(password);
        userInfo.setPassword(encodedPassword);
        // Save user into SQL table
        userRepo.save(userInfo);
        CookieUtils.createLoginCookie(httpResponse, userInfo, userSessionsRepo);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> postLogin(@RequestBody Map<String, String> loginInfo,
            HttpServletResponse response, HttpServletRequest request) {
        
        Cookie[] cookies = request.getCookies();

        for(int i = 0; i < cookies.length; i++) {
            if(cookies[i].getName().equals("session_id")) {
                Optional<UserSessions> sessionMap = userSessionsRepo.findBySessionId(cookies[i].getValue());
                if(sessionMap.isPresent()) {
                    UserInfo user = sessionMap.get().getUser();
                    CookieUtils.updateCookie(cookies[i], userSessionsRepo, sessionMap.get(), response);
                    UserDTO userDTO = new UserDTO(user);
                    return ResponseEntity.ok(userDTO);
                }else {
                    break;
                }
            }
        }
        
        String username = loginInfo.get("username");
        String password = loginInfo.get("password");
        Optional<UserInfo> info = userRepo.findByUsername(username);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Check that a user does indeed exist under the username
        if (info.isPresent()) {
            // Check that the user has entered the correct password for the email
            if (encoder.matches(password, info.get().getPassword())) {
                CookieUtils.createLoginCookie(response, info.get(), userSessionsRepo);
                UserDTO userDTO = new UserDTO(info.get());
                return ResponseEntity.ok(userDTO);
            }
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.ok(null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Boolean> postLogout(HttpServletRequest request) {
        return ResponseEntity.ok(true);
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
