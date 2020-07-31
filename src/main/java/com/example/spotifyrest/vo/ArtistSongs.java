package com.example.spotifyrest.vo;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class ArtistSongs {

    @NonNull
    private String artistName;

    @NonNull
    private List<String> songIds;
}
