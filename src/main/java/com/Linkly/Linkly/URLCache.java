package com.Linkly.Linkly;

import com.Linkly.Linkly.URLModel;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class URLCache {

    private final ConcurrentHashMap<String, URLModel> cache = new ConcurrentHashMap<>();

    // Get Url model by shortId (newURL)
    public URLModel get(String shortId) {
        return cache.get(shortId);
    }

    // Put Url model into cache
    public void put(String shortId, URLModel url) {
        cache.put(shortId, url);
    }

    // Increment clicks inside cache (and return updated Url)
    public URLModel incrementClick(String shortId) {
        return cache.computeIfPresent(shortId, (id, url) -> {
            url.setClickCount(url.getClickCount() + 1);
            return url;
        });
    }

    public void evict(String shortId) {
        cache.remove(shortId);
    }
}

