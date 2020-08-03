package com.example.spotifyrest.controller;

import com.example.spotifyrest.model.SongsDocument;
import com.example.spotifyrest.service.ArtistServiceable;
import com.example.spotifyrest.vo.Artist;
import com.example.spotifyrest.vo.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/artist/")
public class ArtistController {

    @Autowired
    ArtistServiceable artistServiceable;

    @GetMapping(value = "{artistName}")
    public ResponseEntity<List<Song>> getSongByArtistName(@PathVariable String artistName){
        return new ResponseEntity<>(artistServiceable.getSongsByArtist(artistName), HttpStatus.OK);
    }

    @DeleteMapping(value = "{artistName}")
    public ResponseEntity<List<SongsDocument>> deleteSongByArtistName(@PathVariable String artistName){
        return new ResponseEntity<>(artistServiceable.deleteAllSongsOfArtist(artistName), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "")
    public ResponseEntity<String> artistCreation(@RequestBody Artist artistProfile, @RequestParam MultipartFile artistImage){
        return new ResponseEntity<>(artistServiceable.createArtist(artistProfile, artistImage), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "{artistName}/playlist/{playlistId}")
    public ResponseEntity<String> playlistAdditionToArtistProfile(@PathVariable String artistName, @PathVariable String playlistId){
        return  new ResponseEntity<>(artistServiceable.addPlaylistToArtist(artistName, playlistId), HttpStatus.OK);
    }
//    @PostMapping(value = "{artistName}")
//    public ResponseEntity<String> artistCreation(@PathVariable String artistName){
//        return new ResponseEntity<>(artistServiceable.createArtist(artistName), HttpStatus.OK);
//    }
//
//    @PostMapping(value = "")
//    public ResponseEntity<String> addSongToArtistDocument(@RequestBody ArtistSongs artistSongs){
//        return new ResponseEntity<>(artistServiceable.addSongToArtist(artistSongs.getArtistName(), artistSongs.getSongIds(), true), HttpStatus.OK);
//    }
}
