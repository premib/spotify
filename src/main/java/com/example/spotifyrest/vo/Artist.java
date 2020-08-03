package com.example.spotifyrest.vo;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

@Data
@ToString
public class Artist {

    @NonNull
    private String artistName;

    private String artistDescription;
}
