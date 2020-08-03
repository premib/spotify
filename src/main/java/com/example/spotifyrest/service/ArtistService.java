package com.example.spotifyrest.service;

import com.example.spotifyrest.exception.ServiceException;
import com.example.spotifyrest.model.ArtistDocument;
import com.example.spotifyrest.model.SongsDocument;
import com.example.spotifyrest.repository.ArtistRepository;
import com.example.spotifyrest.repository.SongsRepository;
import com.example.spotifyrest.vo.Artist;
import com.example.spotifyrest.vo.Song;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
public class ArtistService implements ArtistServiceable{

    @Autowired
    SongsRepository songsRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    GridFsTemplate gridFsTemplate;



    @Override
    public List<Song> getSongsByArtist(String artistName) {

        List<Song> songListOfArtist = new LinkedList<>();
        Song songObject;
        Query query = new Query();
        query.addCriteria(Criteria.where("artistName").is(artistName)).fields().include("songIds");

        List<SongsDocument> artistRetrievedSong = mongoTemplate.find(query, SongsDocument.class);

        for(SongsDocument song : artistRetrievedSong){
            songObject = modelMapper.map(song, Song.class);
            songListOfArtist.add(songObject);
        }

        return songListOfArtist;
    }

    @Override
    public List<SongsDocument> deleteAllSongsOfArtist(String artistName) {

        return songsRepository.deleteByArtistName(artistName);
    }

    @Override
    public String createArtist(Artist artist, MultipartFile artistImage){

        if(artistRepository.existsByArtistName(artist.getArtistName()))
            throw new ServiceException("artist already exists", HttpStatus.CONFLICT);

        ArtistDocument newArtist = modelMapper.map(artist, ArtistDocument.class);
        try {
            ObjectId artistImageId = gridFsTemplate.store(artistImage.getInputStream(),
                    artistImage.getOriginalFilename()+"."+Objects.requireNonNull(artistImage.getContentType())
                            .strip()
                            .split("/")[1],
                            artistImage.getContentType());
            newArtist.setArtistImage(artistImageId.toString());

            return artistRepository.save(newArtist).getId();
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new ServiceException("IO Error: "+exception, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public String addPlaylistToArtist(String artistName, String playlistId){

        Query query = new Query();
        Update update = new Update();

        query.addCriteria(Criteria.where("artistName").is(artistName));
        update.push("relatedPlaylist", playlistId);
        UpdateResult updatedArtist = mongoTemplate.upsert(query, update, ArtistDocument.class);

        return Objects.requireNonNull(updatedArtist.getUpsertedId()).toString();
    }

}
