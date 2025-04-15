package com.parker.url.url_shortener.UserSessions;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSessionsRepository extends JpaRepository<UserSessions, Long> {
    Optional<UserSessions> findBySessionId(String sessionId);
}
