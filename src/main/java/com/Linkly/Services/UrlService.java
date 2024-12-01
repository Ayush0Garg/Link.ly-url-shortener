package com.example.Linkly.Services;
import java.math.BigInteger;
import java.security.MessageDigest;

import com.example.Linkly.Models.CustomUrlModel;
import com.example.Linkly.Models.UrlModel;
import com.example.Linkly.Repository.UrlRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UrlService {

    private static final String characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int setSize = characterSet.length();

    @Autowired
    private UrlRepo urlRepo;

    int expirationTime = 28;

    public String urlShortener(String originalUrl) {

        Optional<UrlModel> urlModel = urlRepo.findByoriginalUrl(originalUrl);

        // if URL is already in database
        if (urlModel.isPresent()){
            return urlModel.get().getShortUrl();
        }

        if (urlRepo.findByshortUrl(originalUrl).isPresent()) return originalUrl;

        String shortUrl = base62encode(hashUrl(originalUrl));

        //save to database
        UrlModel urlModel1 = new UrlModel();
        urlModel1.setShortUrl(shortUrl);
        urlModel1.setOriginalUrl(originalUrl);
        urlModel1.setCreationTime(LocalDateTime.now());
        urlModel1.setExpirationDate(urlModel1.getCreationTime().plusDays(expirationTime));
        urlRepo.save(urlModel1);

        System.out.println("url: " + urlModel1.getOriginalUrl());
        System.out.println("new url: " + urlModel1.getShortUrl());
        System.out.println("time: " + urlModel1.getCreationTime());
        System.out.println("expire: " + urlModel1.getExpirationDate());

        return shortUrl;
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

    public Optional<UrlModel> find(String shortUrl) {

        Optional<UrlModel> url = urlRepo.findByshortUrl(shortUrl);

        if (url.isPresent()) {
            return Optional.of(url.get());
        } else {
            System.out.println("URL not found for shortUrl: " + shortUrl);
            return null;
        }
    }

    public String findUrlredirect(String shortUrl) {
        Optional<UrlModel> urlModel = urlRepo.findByshortUrl(shortUrl);
        if (urlModel.isPresent()){
            int  count = urlModel.get().getCountClicked();
            urlModel.get().setCountClicked(++count);
            urlRepo.save(urlModel.get());
            return urlModel.get().getOriginalUrl();
        }
        System.out.println("URL not valid");
        return null;
    }

    public String customUrl(CustomUrlModel customUrlModel) {
        if (urlRepo.findByshortUrl(customUrlModel.getCustomUrl()).isPresent()){
            return "Already Exists";
        }

        if (urlRepo.findByoriginalUrl(customUrlModel.getOriginalUrl()).isPresent()){
            Optional<UrlModel> urlModel = Optional.of(new UrlModel());
            urlModel = urlRepo.findByoriginalUrl(customUrlModel.getOriginalUrl());
            urlModel.get().setShortUrl(customUrlModel.getCustomUrl());
            urlModel.get().setCreationTime(LocalDateTime.now());
            urlRepo.save(urlModel.get());
            return urlModel.get().getShortUrl();
        }
        else{
            UrlModel urlModel = new UrlModel();
            urlModel.setShortUrl(customUrlModel.getCustomUrl());
            urlModel.setOriginalUrl(customUrlModel.getOriginalUrl());
            urlModel.setCreationTime(LocalDateTime.now());
            urlModel.setExpirationDate(urlModel.getCreationTime().plusDays(expirationTime));
            urlRepo.save(urlModel);
            return urlModel.getShortUrl();
        }

    }

    public List<UrlModel> getAllLinks() {
        return urlRepo.getAllLinksByCountClicked();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void removeExpired(){
        System.out.print("Running scheduled task to remove expired links...");
        urlRepo.removeExpiredLinks();
        System.out.println("Done");
    }


}
