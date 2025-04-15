package com.parker.url.url_shortener.UserSessions;
import java.time.LocalDateTime;

import com.parker.url.url_shortener.AuthenticationAPI.UserInfo;

import jakarta.persistence.*;

@Entity
public class UserSessions {
    @Id
    private String sessionId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInfo user;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public UserSessions() {
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusDays(1);
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public UserInfo getUser() {
        return this.user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return this.expiresAt;
    }

}
