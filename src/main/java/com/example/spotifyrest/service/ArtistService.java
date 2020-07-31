package com.example.spotifyrest.service;

import com.example.spotifyrest.model.SongsDocument;
import com.example.spotifyrest.repository.SongsRepository;
import com.example.spotifyrest.vo.Song;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ArtistService implements ArtistServiceable{

    @Autowired
    SongsRepository songsRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ModelMapper modelMapper;

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

}
