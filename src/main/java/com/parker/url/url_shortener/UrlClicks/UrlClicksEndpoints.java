package com.parker.url.url_shortener.UrlClicks;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
        ZoneId zone = ZoneId.of("UTC");
        Instant startDay = parsedDate.atStartOfDay(zone).toInstant();
        LocalDateTime endTime = parsedDate.atTime(LocalTime.MAX);
        Instant endDay = endTime.atZone(zone).toInstant();
        List<UrlClick> clicks = urlClickRepo.findByUrlMapping_ShortUrlAndClickedAtBetween(shortUrl, startDay, endDay);
        return clicks;
    }

    @GetMapping("/tracking/current/{shortUrl}")
    public List<UrlClick> getCurrentClicks(@PathVariable String shortUrl) {
        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(Duration.ofHours(1));
        List<UrlClick> clicks = urlClickRepo.findByUrlMapping_ShortUrlAndClickedAtBetween(shortUrl, startTime, endTime);
        return clicks;
    }

    @GetMapping("/tracking/daily/{shortUrl}/{startDay}/{endDay}")
    public List<UrlClick> getDailyClicks(@PathVariable String shortUrl, @PathVariable String startDay, @PathVariable String endDay) {
        Instant startTime = Instant.parse(startDay);
        Instant endTime = Instant.parse(endDay);
        ZoneId zone = ZoneId.of("UTC");
        startTime = startTime.atZone(zone)     
                     .toLocalDate()  
                     .atStartOfDay(zone)
                     .toInstant();
        endTime = endTime.atZone(zone)
                    .toLocalDate()
                    .atTime(LocalTime.MAX)
                    .atZone(zone)
                    .toInstant();
        List<UrlClick> clicks = urlClickRepo.findByUrlMapping_ShortUrlAndClickedAtBetween(shortUrl, startTime, endTime);

        return clicks;
    }
}
