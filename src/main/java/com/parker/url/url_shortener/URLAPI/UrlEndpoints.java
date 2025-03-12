package com.parker.url.url_shortener.URLAPI;

import java.util.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UrlEndpoints {
    @Autowired
    private URLMappingRepository urlMappingRepository;
    
    @PostMapping("/shorten")
    public Map<String, String> postMethodName(@RequestBody Map<String, String> request) {
        String longUrl = request.get("url");
        Map<String, String> response = new HashMap<>();
        Optional<URLMapping> map = urlMappingRepository.findByLongUrl(longUrl);
        if(map.isPresent()) {
            response.put("shortUrl", map.get().getShortUrl());
            response.put("new", null);
        }else {
            String shortUrl;
            do {
                shortUrl = generateShortUrl(longUrl);
            }while (urlMappingRepository.findByShortUrl(shortUrl).isPresent());

            URLMapping urlMapping = new URLMapping();
            urlMapping.setShortUrl(shortUrl);
            urlMapping.setLongUrl(longUrl);
            urlMappingRepository.save(urlMapping);
            response.put("shortUrl", shortUrl);
            response.put("new", "true");
        }
        return response;
    }

    @GetMapping("/{shortId}")
    public ResponseEntity<String> getLongUrl(@PathVariable String shortId) {
        String shortUrl = "localhost:3000/" + shortId;
        Optional<URLMapping> map = urlMappingRepository.findByShortUrl(shortUrl);
        if(map.isPresent()) {
            return ResponseEntity.ok(map.get().getLongUrl());
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Short Url Not Found");
        }
    }
    
    
    private String generateShortUrl(String longUrl) {
        return "localhost:3000/" + UUID.randomUUID().toString().substring(0, 8);
    }
}
