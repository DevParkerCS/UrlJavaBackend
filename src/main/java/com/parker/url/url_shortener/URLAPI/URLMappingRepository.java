package com.parker.url.url_shortener.URLAPI;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface URLMappingRepository extends JpaRepository<URLMapping, Long>{
    
    Optional<URLMapping> findByLongUrl(String longUrl);
    Optional<URLMapping> findByShortUrl(String shortUrl);
}
