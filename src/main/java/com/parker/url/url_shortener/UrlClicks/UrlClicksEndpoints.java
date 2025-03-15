package com.parker.url.url_shortener.UrlClicks;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UrlClicksEndpoints {
    @Autowired
    UrlClickRepository urlClickRepo;

    @GetMapping("/tracking/{shortUrl}")
    public String getMethodName(@RequestParam String shortUrl) {
        List<UrlClick> clicks = urlClickRepo.findByUrlMapping_ShortUrl(shortUrl);
        System.out.println(clicks);
        return new String();
    }
    
}
