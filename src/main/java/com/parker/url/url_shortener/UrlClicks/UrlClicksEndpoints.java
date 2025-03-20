package com.parker.url.url_shortener.UrlClicks;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class UrlClicksEndpoints {
    @Autowired
    UrlClickRepository urlClickRepo;

    @GetMapping("/tracking/{shortUrl}")
    public List<UrlClick> getMonthlyClicks(@PathVariable String shortUrl) {
        List<UrlClick> clicks = urlClickRepo.findByUrlMapping_ShortUrl(shortUrl);
        return clicks;
    }
    
    @GetMapping("/tracking/hourly/{shortUrl}/{day}")
    public List<UrlClick> getDayClicks(@PathVariable String shortUrl, @PathVariable String day) {
        LocalDate parsedDate = LocalDate.parse(day, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime startDay = parsedDate.atStartOfDay();
        LocalDateTime endDay = parsedDate.atTime(LocalTime.MAX);
        List<UrlClick> clicks = urlClickRepo.findByUrlMapping_ShortUrlAndClickedAtBetween(shortUrl, startDay, endDay);
        return clicks;
    }

    @GetMapping("/tracking/current/{shortUrl}")
    public List<UrlClick> getCurrentClicks(@PathVariable String shortUrl) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(1);
        List<UrlClick> clicks = urlClickRepo.findByUrlMapping_ShortUrlAndClickedAtBetween(shortUrl, startTime, endTime);
        return clicks;
    }

    @GetMapping("/tracking/daily/{shortUrl}/{startDay}/{endDay}")
    public List<UrlClick> getDailyClicks(@PathVariable String shortUrl, @PathVariable String startDay, @PathVariable String endDay) {
        // Parse ISO time into LocalDate object
        LocalDate start = LocalDate.parse(startDay, DateTimeFormatter.ISO_DATE_TIME);
        // Turn start date into DateTime object
        LocalDateTime startTime = start.atStartOfDay();
        LocalDate end = LocalDate.parse(endDay, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime endTime = end.atTime(LocalTime.MAX);
        List<UrlClick> clicks = urlClickRepo.findByUrlMapping_ShortUrlAndClickedAtBetween(shortUrl, startTime, endTime);

        return clicks;
    }
}
