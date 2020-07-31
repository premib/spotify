package com.example.spotifyrest.service;

import com.example.spotifyrest.model.SongsDocument;
import com.example.spotifyrest.vo.Song;

import java.util.List;

public interface ArtistServiceable {

    List<Song> getSongsByArtist(String artistName);

    List<SongsDocument> deleteAllSongsOfArtist(String artistName);
}
