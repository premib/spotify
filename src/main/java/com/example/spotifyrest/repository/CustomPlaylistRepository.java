package com.example.spotifyrest.repository;

import com.example.spotifyrest.model.CustomPlaylistDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CustomPlaylistRepository extends MongoRepository<CustomPlaylistDocument, String> {

    public boolean existsById(String id);

    public Optional<CustomPlaylistDocument> findById(String id);
}
