package com.parker.url.url_shortener.UrlClicks;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class UrlClicksEndpoints {
    @Autowired
    UrlClickRepository urlClickRepo;

    @GetMapping("/tracking/{shortUrl}")
    public List<UrlClick> getMethodName(@PathVariable String shortUrl) {
        List<UrlClick> clicks = urlClickRepo.findByUrlMapping_ShortUrl(shortUrl);
        return clicks;
    }
    
}
