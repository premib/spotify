package com.example.spotifyrest.service;

import com.example.spotifyrest.vo.Playlist;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface CommonPlaylistServiceable {

    String createCommonPlaylist(Playlist newPlaylist, MultipartFile imageFile) throws IOException;

    GridFsResource getImage();
}
