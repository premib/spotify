package com.example.spotifyrest.service;

import com.example.spotifyrest.model.SongsDocument;
import com.example.spotifyrest.vo.Artist;
import com.example.spotifyrest.vo.Song;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArtistServiceable {

    List<Song> getSongsByArtist(String artistName);

    List<SongsDocument> deleteAllSongsOfArtist(String artistName);

    String createArtist(Artist artist, MultipartFile artistImage);

    String addPlaylistToArtist(String artistName, String playlistId);
}
