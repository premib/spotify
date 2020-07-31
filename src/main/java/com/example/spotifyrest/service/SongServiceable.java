package com.example.spotifyrest.service;

import com.example.spotifyrest.vo.ListSong;
import com.example.spotifyrest.vo.InsertionResponse;
import com.example.spotifyrest.vo.Song;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SongServiceable {

    InsertionResponse addSongs(String artistName, MultipartFile[] audioFile);

    ListSong deleteSongs(ListSong songsToDelete);

    Song getSong(String songId);

    List<Song> getSongDetails(ListSong songs);

    Song modifySongDetail(Song song);
}
