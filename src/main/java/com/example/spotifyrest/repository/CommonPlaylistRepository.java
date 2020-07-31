package com.example.spotifyrest.repository;

import com.example.spotifyrest.model.CommonPlaylistDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommonPlaylistRepository extends MongoRepository<CommonPlaylistDocument, String> {

    public boolean existsByPlaylistName(String playlistName);
}
