package com.Linkly.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table (name = "url_model")
public class UrlModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int countClicked = 0;

    private LocalDateTime creationTime;

    private String originalUrl;

    @Column (unique = true)
    private String shortUrl;

}
