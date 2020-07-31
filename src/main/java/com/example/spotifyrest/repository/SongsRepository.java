package com.example.spotifyrest.repository;

import com.example.spotifyrest.model.SongsDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface SongsRepository extends MongoRepository<SongsDocument, String> {

    public boolean existsById(String id);

    public Optional<SongsDocument> findById(String id);

    @Transactional
    public void deleteById(String id);

    public boolean existsByArtistName(String artistName);

    public List<SongsDocument> deleteByArtistName(String artistName);

    public boolean existsBySongName(String musicName);
}
