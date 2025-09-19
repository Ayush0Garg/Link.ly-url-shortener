package com.Linkly.Linkly;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomUrl {
    private String originalUrl;
    private String customUrl;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getCustomUrl() {
        return customUrl;
    }

    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }
}
