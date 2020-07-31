package com.example.spotifyrest.vo;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class ListSong {

    List<String> songIds;
    List<String> deleted = new ArrayList<>();
    List<String> ignored = new ArrayList<>();
}
