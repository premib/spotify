package com.example.spotifyrest.repository;

import com.example.spotifyrest.model.ArtistDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArtistRepository extends MongoRepository<ArtistDocument, String> {

    boolean existsByArtistName(String artistName);

}
