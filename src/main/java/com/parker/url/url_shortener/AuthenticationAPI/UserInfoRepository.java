package com.parker.url.url_shortener.AuthenticationAPI;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByUsername(String email);
}
