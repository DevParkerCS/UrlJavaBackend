package com.parker.url.url_shortener.AuthenticationAPI;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    // Method to find users by indexed email address
    Optional<UserInfo> findByEmail(String email);

    Optional<UserInfo> findByUsername(String email);
}
