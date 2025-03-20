package com.parker.url.url_shortener.UrlClicks;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlClickRepository extends JpaRepository<UrlClick, Long> {
    List<UrlClick> findByUrlMapping_ShortUrl(String shortUrl);
    List<UrlClick> findByUrlMapping_ShortUrlAndClickedAtBetween(String shortUrl, Instant startTime, Instant endTime);
}
