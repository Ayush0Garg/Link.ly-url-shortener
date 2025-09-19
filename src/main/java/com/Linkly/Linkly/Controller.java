package com.Linkly.Linkly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;


import java.util.List;

@RestController
@RequestMapping("/linkly")
public class Controller {

    @Autowired
    private URLService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenURL(@RequestBody URLModel urlModel){
        String originalUrl = urlModel.getOriginalUrl();
        String shortUrl = urlService.shortenURL(originalUrl);
        return ResponseEntity.ok().body(shortUrl);
    }

    @PostMapping("/shorten/custom")
    public ResponseEntity<String> customUrl(@RequestBody CustomUrl customUrl){
        String originalURL = customUrl.getOriginalUrl();
        String customURl = urlService.customUrl(originalURL, customUrl);
        return ResponseEntity.ok().body(customURl);
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<URLModel>> getAllUrls(){
        List<URLModel> allUrls;
        allUrls = urlService.getAllUrls();
        return ResponseEntity.ok().body(allUrls);
    }

    @GetMapping("/{shortUrl}")
    public RedirectView redirectUrl(@PathVariable String shortUrl){
        String originalUrl = urlService.findUrlRedirect(shortUrl);
        return new RedirectView(originalUrl);
    }









}
