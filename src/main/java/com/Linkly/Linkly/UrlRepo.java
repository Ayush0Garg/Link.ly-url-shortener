package com.Linkly.Linkly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepo extends JpaRepository<URLModel, Long> {

    Optional<URLModel> findByNewUrl(String newURL);
    Optional<URLModel> findByOriginalUrl(String originalURL);

    @Query(value = "SELECT * FROM url", nativeQuery = true)
    List<URLModel> getAllUrls();

    @Modifying
    @Query("DELETE FROM URLModel u WHERE u.expireDate < CURRENT_TIMESTAMP")
    void removeExpiredLinks();
}
