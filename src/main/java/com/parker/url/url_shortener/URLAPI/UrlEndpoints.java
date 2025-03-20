package com.parker.url.url_shortener.URLAPI;

import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.parker.url.url_shortener.UrlShortenerApplication;
import com.parker.url.url_shortener.UrlClicks.UrlClick;
import com.parker.url.url_shortener.UrlClicks.UrlClickRepository;

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
    @Autowired
    private UrlClickRepository urlClickRepo;

    UrlEndpoints(UrlShortenerApplication urlShortenerApplication) {
        this.urlShortenerApplication = urlShortenerApplication;

    }
    
    @PostMapping("/shorten")
    public ResponseEntity<URLMapping> postUrlShorten(@RequestBody Map<String, String> request) {
        String rawLongUrl = request.get("url");
        String longUrl = normalizeUrl(rawLongUrl);

        if(longUrl.equals("")) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }

        if(!isUrlValid(longUrl)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
        Optional<URLMapping> map = urlMappingRepository.findByLongUrl(longUrl);

        // Check if the url has already been shortened
        if(map.isPresent()) {
            return ResponseEntity.ok(map.get());
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
            urlMapping.setTotalClicks(0L);
            // Save new mapping to table
            urlMappingRepository.save(urlMapping);
            return ResponseEntity.ok(urlMapping);
        }
    }

    @GetMapping("/{shortId}")
    public ResponseEntity<String> getLongUrl(@PathVariable String shortId) {
        String shortUrl = shortId;
        Optional<URLMapping> map = urlMappingRepository.findByShortUrl(shortUrl);
        // Ensure the short url is a valid url
        if(map.isPresent()) {
            URLMapping urlMap = map.get();
            urlMap.addClick();
            UrlClick newClick = new UrlClick();
            newClick.setIpAddress("1.1.1.1");
            newClick.setUrlMapping(urlMap);
            urlMap.setTotalClicks(urlMap.getTotalClicks() + 1);
            urlClickRepo.save(newClick);
            urlMappingRepository.save(urlMap);
            return ResponseEntity.ok(map.get().getLongUrl());
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Short Url Not Found");
        }
    }
    
    
    private String generateShortUrl(String longUrl) {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private boolean isUrlValid(String url) {
        String urlRegex = "^(https?:\\/\\/)?(www?\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(\\/.*)?$";
        Pattern pattern = Pattern.compile(urlRegex);

        // Check if the URL contains multiple occurrences of "https?://"
        if (url.split("https?://").length >= 2) {
            return false;  
        }
    
        // Check if the URL contains multiple occurrences of "www."
        if (url.split("www\\.").length >= 2) {
            return false; 
        }
    

        return pattern.matcher(url).matches();
    }
    
    public String normalizeUrl(String url) {
        // Define the regex pattern
        String regex = "^(https?://)?(www\\.)?([^/]+)(/.*)?$";
        
        // Create a Pattern object
        Pattern pattern = Pattern.compile(regex);
        
        // Create a matcher to match the URL
        Matcher matcher = pattern.matcher(url);
        
        // Check if the URL matches the regex
        if (matcher.matches()) {
            // Extract the domain and the path
            String domain = matcher.group(3);  // Group 3 contains the domain
            String path = matcher.group(4) != null ? matcher.group(4) : "";  // Group 4 contains the path (if any)
            
            // Return the normalized URL (domain + path)
            return domain + path;
        }
        
        // Return empty string if no match
        return "";
    }
}

