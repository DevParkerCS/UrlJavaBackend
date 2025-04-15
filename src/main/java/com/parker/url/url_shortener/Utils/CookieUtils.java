package com.parker.url.url_shortener.Utils;

import java.time.LocalDateTime;
import java.util.UUID;

import com.parker.url.url_shortener.AuthenticationAPI.UserInfo;
import com.parker.url.url_shortener.UserSessions.UserSessions;
import com.parker.url.url_shortener.UserSessions.UserSessionsRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtils {
    public static void createLoginCookie(HttpServletResponse response, UserInfo user, UserSessionsRepository userSessionsRepo) {
        // Create new login cookie with unique session id
        String sessionId = UUID.randomUUID().toString();
        Cookie loginCookie = new Cookie("session_id", sessionId);

        // Set cookie attributes
        loginCookie.setMaxAge(86400);
        loginCookie.setSecure(false);
        loginCookie.setHttpOnly(true);
        loginCookie.setAttribute("SameSite", "Strict");

        UserSessions userSession = new UserSessions();
        userSession.setSessionId(sessionId);
        userSession.setUser(user);
        userSessionsRepo.save(userSession);

        // Add cookie to the response
        response.addCookie(loginCookie);
    }

    public static void updateCookie(Cookie cookie, UserSessionsRepository userSessionsRepo, UserSessions userSession, HttpServletResponse response) {
        Cookie updatedCookie = new Cookie("session_id", cookie.getValue());

        // Set cookie attributes
        updatedCookie.setMaxAge(86400);
        updatedCookie.setSecure(false);
        updatedCookie.setHttpOnly(true);
        updatedCookie.setAttribute("SameSite", "Strict");

        userSession.setCreatedAt(LocalDateTime.now());
        userSession.setExpiresAt(userSession.getCreatedAt().plusDays(1));
        userSessionsRepo.save(userSession);

        response.addCookie(updatedCookie);
    }
}
