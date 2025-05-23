package com.parker.url.url_shortener.UrlClicks;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UrlClicksEndpoints {
    @Autowired
    UrlClickRepository urlClickRepo;

    @GetMapping("/tracking/{shortUrl}/{year}")
    public List<UrlClick> getMonthlyClicks(@PathVariable String shortUrl, @PathVariable Integer year, @RequestParam String timeZone) {
        ZoneId zone = ZoneId.of(timeZone);
        LocalDate firstDayOfYear = LocalDate.of(year, 1, 1);
        Instant startOfYear = firstDayOfYear.atStartOfDay(zone).toInstant();

        LocalDate lastDayOfYear = LocalDate.of(year, 12, 31);
        Instant endOfYear = lastDayOfYear.atTime(LocalTime.MAX).atZone(zone).toInstant();
        List<UrlClick> clicks = urlClickRepo.findByUrlMapping_ShortUrlAndClickedAtBetween(shortUrl, startOfYear, endOfYear);
        return clicks;
    }

    @GetMapping("/tracking/current/{shortUrl}")
    public List<UrlClick> getCurrentClicks(@PathVariable String shortUrl, @RequestParam String timeZone) {
        ZoneId zone = ZoneId.of(timeZone);
        Instant endTime = ZonedDateTime.now(zone).toInstant();
        Instant startTime = endTime.minus(Duration.ofHours(1));
        List<UrlClick> clicks = urlClickRepo.findByUrlMapping_ShortUrlAndClickedAtBetween(shortUrl, startTime, endTime);
        return clicks;
    }

    @GetMapping("/tracking/range/{shortUrl}/{startDay}/{endDay}")
    public List<UrlClick> getClicksInRange(@PathVariable String shortUrl, @PathVariable String startDay, @PathVariable String endDay, @RequestParam String timeZone) {
        Instant startTime = Instant.parse(startDay);
        Instant endTime = Instant.parse(endDay);
        ZoneId zone = ZoneId.of(timeZone);
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
