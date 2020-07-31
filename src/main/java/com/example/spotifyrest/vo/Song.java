package com.example.spotifyrest.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Song {

    private String id;
    private String songName;
    private String artistName;
    private String songType;
    private byte[] songBytes;
}
