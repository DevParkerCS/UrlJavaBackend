package com.parker.url.url_shortener.UrlClicks;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.parker.url.url_shortener.URLAPI.URLMapping;

import jakarta.persistence.*;

@Entity
@Table(name = "url_clicks")
public class UrlClick {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "url_mapping_id", nullable = false)
    private URLMapping urlMapping;

    private Instant clickedAt;
    private String ipAddress;

    public UrlClick() {
        this.clickedAt = Instant.now();
    }

    // Getters and Setters
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public URLMapping getUrlMapping() {
        return this.urlMapping;
    }

    public void setUrlMapping(URLMapping urlMapping) {
        this.urlMapping = urlMapping;
    }

    public Instant getClickedAt() {
        return this.clickedAt;
    }

    public void setClickedAt(Instant clickedAt) {
        this.clickedAt = clickedAt;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

}
