package com.Linkly.Repository;

import com.Linkly.Models.UrlModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepo extends JpaRepository<UrlModel, Long> {

    Optional<UrlModel> findByshortUrl(String shortUrl);
    Optional<UrlModel> findByoriginalUrl(String originalUrl);
}
