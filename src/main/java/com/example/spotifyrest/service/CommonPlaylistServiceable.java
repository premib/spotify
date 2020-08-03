package com.example.spotifyrest.service;

import com.example.spotifyrest.vo.InsertionResponse;
import com.example.spotifyrest.vo.ListSong;
import com.example.spotifyrest.vo.Playlist;
import com.example.spotifyrest.vo.Song;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CommonPlaylistServiceable {

    String createCommonPlaylist(Playlist newPlaylist, MultipartFile imageFile) throws IOException;

    Playlist getPlaylist(String playlistId);

    Playlist getPlaylistByName(String playlistName);

    InsertionResponse addSongToPlaylist(String playlistId, ListSong songs);

    List<Song> getSongDetails(String playlistId);

    String deletePlaylist(String playlistId);
}
