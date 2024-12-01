package com.example.Linkly.Controllers;
import com.example.Linkly.Models.CustomUrlModel;
import com.example.Linkly.Models.UrlModel;
import com.example.Linkly.Services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/link.ly")
public class UrlController {

    @Autowired
    private UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<String> urlShortener(@RequestBody UrlModel urlModel){
        if (urlModel.getOriginalUrl().length() > 255) {
            urlModel.setOriginalUrl(urlModel.getOriginalUrl().substring(0, 255));
        }

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
        String originalUrl = urlService.findUrlredirect(shortUrl);
        return new RedirectView(originalUrl);
    }

    @PostMapping("/shorten/custom")
    public ResponseEntity<String> customUrl(@RequestBody CustomUrlModel customUrlModel){
        String newUrl = urlService.customUrl(customUrlModel);
        return ResponseEntity.ok(newUrl);
    }

    @GetMapping("/get/mostClicked")
    ResponseEntity<List<UrlModel>> getMostClicked(){
        List<UrlModel> list ;
        list = urlService.getAllLinks();
        return ResponseEntity.ok(list);
    }



}
