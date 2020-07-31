package com.example.spotifyrest.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Playlist {

    private String id;
    private String playlistName;
    private String playlistDescription;
    private boolean premium;
}
