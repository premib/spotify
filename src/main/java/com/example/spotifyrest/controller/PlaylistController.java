package com.example.spotifyrest.controller;

import com.example.spotifyrest.exception.ServiceException;
import com.example.spotifyrest.service.CommonPlaylistServiceable;
import com.example.spotifyrest.vo.InsertionResponse;
import com.example.spotifyrest.vo.ListSong;
import com.example.spotifyrest.vo.Playlist;
import com.example.spotifyrest.vo.Song;
import org.apache.coyote.Response;
import org.hibernate.sql.Insert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/v1/playlist/")
public class PlaylistController {

    @Autowired
    CommonPlaylistServiceable commonPlaylistServiceable;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(path= "", consumes = "multipart/form-data")
    public ResponseEntity<String> commonPlCreation(Playlist playlist, @RequestParam("imageFile") MultipartFile file) throws IOException {
        return new ResponseEntity<>(commonPlaylistServiceable.createCommonPlaylist(playlist,file), HttpStatus.OK);
    }

    @GetMapping(path = "{playlistId}")
    public ResponseEntity<Playlist> get(@PathVariable String playlistId) throws IOException {
        return new ResponseEntity<>(commonPlaylistServiceable.getPlaylist(playlistId) , HttpStatus.OK);
    }

    @GetMapping(path = "name/{playlistName}")
    public ResponseEntity<Playlist> getPlaylistByName(@PathVariable String playlistName){
        return new ResponseEntity<>(commonPlaylistServiceable.getPlaylistByName(playlistName), HttpStatus.OK);
    }

    @GetMapping(path = "{playlistId}/details")
    public ResponseEntity<List<Song>> getPlaylistDetails(@PathVariable String playlistId){
        return new ResponseEntity<>(commonPlaylistServiceable.getSongDetails(playlistId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(path = "{playlistId}/update")
    public ResponseEntity<InsertionResponse> songAddtion(@PathVariable String playlistId, @RequestBody ListSong listSong){
        return new ResponseEntity<>(commonPlaylistServiceable.addSongToPlaylist(playlistId, listSong), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(path = "{playlistId}")
    public ResponseEntity<String> playlistDeletion(@PathVariable String playlistId){
        return new ResponseEntity<>(commonPlaylistServiceable.deletePlaylist(playlistId), HttpStatus.OK);
    }

}
