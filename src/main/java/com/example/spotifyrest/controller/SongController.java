package com.example.spotifyrest.controller;


import com.example.spotifyrest.service.SongServiceable;
import com.example.spotifyrest.vo.ListSong;
import com.example.spotifyrest.vo.InsertionResponse;
import com.example.spotifyrest.vo.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("v1/music/")
public class SongController {

    @Autowired
    SongServiceable songServiceable;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "")
    public ResponseEntity<InsertionResponse> audio(@RequestParam("audioFile") MultipartFile[] audio) throws IOException {
        return new ResponseEntity<>(songServiceable.addSongs("artist1", audio), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "")
    public ResponseEntity<ListSong> deleteAudio(@RequestBody ListSong songsToDelete){
        return new ResponseEntity<>(songServiceable.deleteSongs(songsToDelete), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_PREMIUM')")
    @GetMapping(value = "{songId}")
    public ResponseEntity<Song> retrieveSong(@PathVariable String songId){
        return new ResponseEntity<>(songServiceable.getSong(songId), HttpStatus.OK);
    }

    @GetMapping(value = "detail")
    public ResponseEntity<List<Song>> retrieveSongsDetail(@RequestBody ListSong songs){
        return new ResponseEntity<>(songServiceable.getSongDetails(songs), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping(value = "edit")
    public ResponseEntity<Song> editSong(@RequestBody Song songEdit) {
        return new ResponseEntity<>(songServiceable.modifySongDetail(songEdit), HttpStatus.OK);
    }

}
