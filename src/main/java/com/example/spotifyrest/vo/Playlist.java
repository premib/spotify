package com.example.spotifyrest.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class Playlist extends Image{

    private String id;
    private String playlistName;
    private String playlistDescription;
    private boolean premium;
}
