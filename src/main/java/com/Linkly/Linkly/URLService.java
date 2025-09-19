package com.Linkly.Linkly;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class URLService {

    @Autowired
    private UrlRepo urlRepo;
    private final String urlPrefix = "";
    private final int expireDays = 30;

    @Autowired
    private URLCache urlCache;

    private static final String characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int setSize = characterSet.length();

    public String shortenURL(String originalUrl) {

        Optional<URLModel> urlModel = urlRepo.findByOriginalUrl(originalUrl);
        if(urlModel.isPresent())
            return urlModel.get().getNewUrl();

        if (urlRepo.findByNewUrl(originalUrl).isPresent()){
            System.out.println("URL Already Shortened");
            return urlModel.get().getOriginalUrl();
        }

        String newUrl = base62encode(hashUrl(originalUrl));

        URLModel urlModel1 = new URLModel();
        urlModel1.setNewUrl(newUrl);
        urlModel1.setOriginalUrl(originalUrl);
        urlModel1.setCreationDate(LocalDateTime.now());
        urlModel1.setExpireDate(LocalDateTime.now().plusDays(expireDays));
        urlModel1.setClickCount(0);

        System.out.printf(
                 "Original URL: %s\n  new URL: %s\n CreationTime: %s\n ExpireDate: %s\n",
                urlModel1.getOriginalUrl(),
                urlModel1.getNewUrl(),
                urlModel1.getCreationDate(),
                urlModel1.getExpireDate()
        );
        urlRepo.save(urlModel1);
        return urlPrefix + newUrl;
    }

    public List<URLModel> getAllUrls() {
        return urlRepo.getAllUrls();
    }

    public String findUrlRedirect(String shortUrl) {

        URLModel url = urlCache.get(shortUrl);
        if (url == null){

            url = urlRepo.findByNewUrl(shortUrl).orElseThrow(() -> new RuntimeException("Not Found The URL"));
            urlCache.put(shortUrl, url);
        }

        url.setClickCount(url.getClickCount() + 1);
        urlRepo.save(url);
        urlCache.put(shortUrl, url);

        return url.getOriginalUrl();
    }

    public String customUrl(String originalURL, CustomUrl customUrl) {
        // Check if the requested custom URL is already taken
        if (urlRepo.findByNewUrl(customUrl.getCustomUrl()).isPresent()) {
            return "Custom URL already exists";
        }

        // Try to find an existing URLModel by the original URL
        Optional<URLModel> existingModel = urlRepo.findByOriginalUrl(customUrl.getOriginalUrl());

        URLModel urlModel;
        String newUrl;

        if (existingModel.isPresent()) {
            // Update existing record
            urlModel = existingModel.get();
            newUrl = customUrl.getCustomUrl() != null && !customUrl.getCustomUrl().isBlank()
                    ? customUrl.getCustomUrl()
                    : base62encode(hashUrl(customUrl.getOriginalUrl()));
            urlModel.setNewUrl(newUrl);
            urlModel.setCreationDate(LocalDateTime.now());
        } else {
            // Create new record
            urlModel = new URLModel();
            newUrl = customUrl.getCustomUrl() != null && !customUrl.getCustomUrl().isBlank()
                    ? customUrl.getCustomUrl()
                    : base62encode(hashUrl(customUrl.getOriginalUrl()));

            urlModel.setOriginalUrl(customUrl.getOriginalUrl());
            urlModel.setNewUrl(newUrl);
            urlModel.setCreationDate(LocalDateTime.now());
            urlModel.setExpireDate(LocalDateTime.now().plusDays(expireDays));
            urlModel.setClickCount(0); // optional, initialize click count
        }

        urlRepo.save(urlModel);
        return urlPrefix + urlModel.getNewUrl();
    }

    private String base62encode(Long num) {
        StringBuilder encoded = new StringBuilder();
        while (num > 0 && encoded.length() < 8){
            int rem = (int) (num % setSize);
            encoded.append(characterSet.charAt(rem));
            num = num / setSize;
        }
        return encoded.reverse().toString();
    }

    private Long hashUrl(String originalUrl) {
        try{
            //add salt to original url
            originalUrl += UUID.randomUUID().toString().replace("-", "").substring(5) ;
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte [] hashUrl = messageDigest.digest(originalUrl.getBytes());
            BigInteger hashValue = new BigInteger(1, hashUrl);
            return Math.abs(hashValue.longValue());
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void removeExpired(){
        System.out.print("Running scheduled task to remove expired links...");
        urlRepo.removeExpiredLinks();
        System.out.println("Done");
    }
}
