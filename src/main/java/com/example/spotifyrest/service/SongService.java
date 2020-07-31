package com.example.spotifyrest.service;

import com.example.spotifyrest.exception.ServiceException;
import com.example.spotifyrest.model.SongsDocument;
import com.example.spotifyrest.repository.SongsRepository;
import com.example.spotifyrest.vo.ListSong;
import com.example.spotifyrest.vo.InsertionResponse;
import com.example.spotifyrest.vo.Song;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class SongService implements SongServiceable{

    @Autowired
    SongsRepository songsRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ArtistService artistService;

    @Override
    public InsertionResponse addSongs(String artistName, MultipartFile[] audioFiles) {

        SongsDocument newSong;
        InsertionResponse response = new InsertionResponse();

        for(MultipartFile audioFile : audioFiles){
            try {

                if(Objects.equals((Objects.requireNonNull(audioFile.getContentType()).strip().split("/"))[0], "audio")){
                    newSong = new SongsDocument();
                    newSong.setSongType(audioFile.getContentType());
                    newSong.setSongName(audioFile.getOriginalFilename());
                    newSong.setArtistName(artistName);
                    newSong.setSongBytes(new Binary(BsonBinarySubType.BINARY, FileService.compressBytes(audioFile.getBytes())));
                    SongsDocument retrievedSongsDocument = songsRepository.save(newSong);
                    response.getAdded().add(retrievedSongsDocument.getId());
                }
                else {
                    response.getIgnored().add(audioFile.getOriginalFilename());
                }
            }
            catch (IOException exception) {
                response.getIgnored().add(audioFile.getOriginalFilename());
            }
        }
        return response;
    }


    @Override
    public ListSong deleteSongs(ListSong songsToDelete) {

        for(String songId : songsToDelete.getSongIds()){
            if(songsRepository.existsById(songId)){
                Query query = new Query();
                mongoTemplate.remove(query.addCriteria(Criteria.where("id").is(songId)), SongsDocument.class);
                songsToDelete.getDeleted().add(songId);
            }
            else{
                songsToDelete.getIgnored().add(songId);
            }
        }
        songsToDelete.setSongIds(null);
        return songsToDelete;
    }

    @Override
    public Song getSong(String songId) {

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(songId));

        if (songsRepository.existsById(songId)) {
            SongsDocument retrievedSongDocument = mongoTemplate.findOne(query, SongsDocument.class);
            Song retrievedSong = modelMapper.map(retrievedSongDocument, Song.class);
            retrievedSong.setSongBytes(FileService
                    .decompressBytes(Objects.requireNonNull(retrievedSongDocument)
                            .getSongBytes()
                            .getData()));
            return retrievedSong;
        }
        else
            throw new ServiceException("Song doesn't exists", HttpStatus.NO_CONTENT);
    }

    @Override
    public List<Song> getSongDetails(ListSong songs) {

        List<Song> songList;
        if(songs.getSongIds().size() < 1){
            throw new ServiceException("Need at least one songId to remove", HttpStatus.BAD_REQUEST);
        }
        else{
            songList = new ArrayList<>();
            for(String songId : songs.getSongIds()){

                Song song = new Song();
                Query query = new Query();

                if(songsRepository.existsById(songId)) {
                    query.addCriteria(Criteria.where("id").is(songId)).fields().exclude("songBytes");
                    SongsDocument retrievedSong = mongoTemplate.findOne(query, SongsDocument.class);
                    songList.add(modelMapper.map(retrievedSong, Song.class));
                }
                else{
                    song.setSongName("0");
                    songList.add(song);
                }
            }
        }
        return songList;
    }

    @Override
    public Song modifySongDetail(Song song) {

        if(song.getId() == null){
            throw new ServiceException("Id should not be null", HttpStatus.BAD_REQUEST);
        }

        SongsDocument songAfterEdit = new SongsDocument();
        InsertionResponse in = new InsertionResponse();

        Optional<SongsDocument> songCurrentValue = songsRepository.findById(song.getId());
        if(songCurrentValue.isPresent()){
             if(song.getArtistName() != null){
                 songCurrentValue.get().setArtistName(song.getArtistName());
             }
             if(song.getSongName() != null){
                 songCurrentValue.get().setSongName(song.getSongName());
             }
             if(song.getSongType() != null){
                 if(Objects.equals(song.getSongType().strip().split("/")[0], "audio")){
                     songCurrentValue.get().setSongType(song.getSongType());
                 }
             }
             if(song.getSongBytes() != null){
                 songCurrentValue.get().setSongBytes(new Binary(BsonBinarySubType.BINARY, FileService.compressBytes(song.getSongBytes())));
             }
             songAfterEdit = songsRepository.save(songCurrentValue.get());
             return modelMapper.map(songAfterEdit, Song.class);
        }
        else{
            throw new ServiceException("Provided SongId is wrong", HttpStatus.BAD_REQUEST);
        }
    }

}
