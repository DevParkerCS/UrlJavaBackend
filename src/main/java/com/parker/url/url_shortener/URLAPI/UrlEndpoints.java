package com.parker.url.url_shortener.URLAPI;

import java.util.*;
import java.util.regex.Pattern;

import com.parker.url.url_shortener.UrlShortenerApplication;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UrlEndpoints {

    private final UrlShortenerApplication urlShortenerApplication;
    @Autowired
    private URLMappingRepository urlMappingRepository;

    UrlEndpoints(UrlShortenerApplication urlShortenerApplication) {
        this.urlShortenerApplication = urlShortenerApplication;
    }
    
    @PostMapping("/shorten")
    public ResponseEntity<Map<String, String>> postUrlShorten(@RequestBody Map<String, String> request) {
        String longUrl = request.get("url");

        if(!isUrlValid(longUrl)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }

        Map<String, String> response = new HashMap<>();
        Optional<URLMapping> map = urlMappingRepository.findByLongUrl(longUrl);

        // Check if the url has already been shortened
        if(map.isPresent()) {
            // Add the already shortened url to the response
            response.put("shortUrl", map.get().getShortUrl());
            response.put("new", null);
        }else {
            String shortUrl;
            // Continue creating a short url until a unique one is made.  There are over 4 billion combinations so this is unlikely
            do {
                shortUrl = generateShortUrl(longUrl);
            }while (urlMappingRepository.findByShortUrl(shortUrl).isPresent());

            // Add new url mapping info
            URLMapping urlMapping = new URLMapping();
            urlMapping.setShortUrl(shortUrl);
            urlMapping.setLongUrl(longUrl);
            // Save new mapping to table
            urlMappingRepository.save(urlMapping);
            // Add information for client response
            response.put("shortUrl", shortUrl);
            response.put("new", "true");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortId}")
    public ResponseEntity<String> getLongUrl(@PathVariable String shortId) {
        String shortUrl = "localhost:3000/" + shortId;
        Optional<URLMapping> map = urlMappingRepository.findByShortUrl(shortUrl);
        // Ensure the short url is a valid url
        if(map.isPresent()) {
            return ResponseEntity.ok(map.get().getLongUrl());
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Short Url Not Found");
        }
    }
    
    
    private String generateShortUrl(String longUrl) {
        return "localhost:3000/" + UUID.randomUUID().toString().substring(0, 8);
    }

    private boolean isUrlValid(String url) {
        String emailRegex = "^(https?:\\/\\/)?(www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(\\/.*)?$";
        Pattern pattern = Pattern.compile(emailRegex);

        return pattern.matcher(url).matches();
    }
}
