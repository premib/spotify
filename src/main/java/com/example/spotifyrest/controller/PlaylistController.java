package com.example.spotifyrest.controller;

import com.example.spotifyrest.exception.ServiceException;
import com.example.spotifyrest.service.CommonPlaylistServiceable;
import com.example.spotifyrest.vo.Playlist;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/v1/playlist/")
public class PlaylistController {

    @Autowired
    CommonPlaylistServiceable commonPlaylistServiceable;

    @PostMapping(path= "", consumes = "multipart/form-data")
    public ResponseEntity<String> commonPlCreation(Playlist playlist, @RequestParam("imageFile") MultipartFile file) throws IOException {
        return new ResponseEntity<>(commonPlaylistServiceable.createCommonPlaylist(playlist,file), HttpStatus.OK);
    }

    @GetMapping(path = "")
    public ResponseEntity<GridFsResource> get(){
        return new ResponseEntity<>(commonPlaylistServiceable.getImage(), HttpStatus.OK);
    }
}
