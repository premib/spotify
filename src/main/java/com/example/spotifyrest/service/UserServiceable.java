package com.example.spotifyrest.service;

import com.example.spotifyrest.vo.AuthResponse;
import com.example.spotifyrest.vo.ListSong;
import com.example.spotifyrest.vo.Register;
import com.example.spotifyrest.vo.UserLogin;

import java.util.List;

public interface UserServiceable {

    AuthResponse registerNewUser(Register newUser);

    AuthResponse login(UserLogin credential);

    String createCustomPlaylist(String name, String email);

    AuthResponse addSongsToCustomPlaylist(String name, String email, List<String> songsMongoId);

    ListSong deleteSongFromCustomPlaylist(String playlistName, String email, ListSong songIds);

    String followArtist(String artistId, String email);

    String unFollowArtist(String artistId, String email);

    String deleteCustomPlaylist(String playlistName, String email);

    AuthResponse upgradeToPremium(String email);

    AuthResponse downgradeToUser(String email);
}
