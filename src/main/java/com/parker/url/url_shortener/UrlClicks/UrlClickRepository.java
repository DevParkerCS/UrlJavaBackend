package com.parker.url.url_shortener.UrlClicks;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlClickRepository extends JpaRepository<UrlClick, Long> {
    List<UrlClick> findByUrlMapping_ShortUrl(String shortUrl);
}
