package com.example.spotifyrest.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
public class AddSongToCPL {

    String playlistName;

    List<String> songId;
}
