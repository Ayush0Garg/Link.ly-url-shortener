package com.Linkly.Controllers;
import com.Linkly.Models.CustomUrlModel;
import com.Linkly.Models.UrlModel;
import com.Linkly.Services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@RestController
@RequestMapping("/link.ly")
public class UrlController {

    @Autowired
    private UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<String> urlShortener(@RequestBody UrlModel urlModel){
        String shortUrl = urlService.urlShortener(urlModel.getOriginalUrl());
        return ResponseEntity.ok(shortUrl);
    }
    
    @GetMapping("/get/{shortUrl}")
    public ResponseEntity<Optional<UrlModel>> find(@PathVariable String shortUrl){
        Optional<UrlModel> urlModel = urlService.find(shortUrl);
        return ResponseEntity.ok(urlModel);
    }

    @GetMapping("/{shortUrl}")
    public RedirectView redirectUrl(@PathVariable String shortUrl){
        String originalUrl = urlService.findUrl(shortUrl);
        return new RedirectView(originalUrl);
    }

    @PostMapping("/shorten/custom")
    public ResponseEntity<String> customUrl(@RequestBody CustomUrlModel customUrlModel){
        String newUrl = urlService.customUrl(customUrlModel);
        return ResponseEntity.ok(newUrl);
    }



}
