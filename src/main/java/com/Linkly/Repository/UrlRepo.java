package com.example.Linkly.Repository;

import com.example.Linkly.Models.UrlModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepo extends JpaRepository<UrlModel, Long> {

    Optional<UrlModel> findByshortUrl(String shortUrl);
    Optional<UrlModel> findByoriginalUrl(String originalUrl);

    @Query(value = "DELETE FROM url_model WHERE expiration_date > CURRENT_DATE", nativeQuery = true)
    void removeExpiredLinks();
    @Query(value = "Select * from url_model order by count_clicked DESC", nativeQuery = true)
    List<UrlModel> getAllLinksByCountClicked ();

}
