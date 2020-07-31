package com.example.spotifyrest.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class AddSongToCPL {

    String playlistName;

    List<String> songId;
}
