package com.parker.url.url_shortener.URLAPI;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "url_mapping", indexes = {
    @Index(name = "idx_short", columnList = "shortUrl"),
    @Index(name = "idx_long", columnList = "longUrl")
})
public class URLMapping {    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(unique = true)
    private String shortUrl;
    
    private String longUrl;

    private Long totalClicks;

    public URLMapping() {
        this.totalClicks = 0L;
    }

    public void addClick() {
        this.totalClicks++;
    }

    public Long getTotalClicks() {
        return this.totalClicks;
    } 

    public void setTotalClicks(Long clicks) {
        this.totalClicks = clicks;
    } 

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortUrl() {
        return this.shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getLongUrl() {
        return this.longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

}
