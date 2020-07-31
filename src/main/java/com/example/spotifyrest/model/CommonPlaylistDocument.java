package com.example.spotifyrest.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "CommonPlaylistDocument")
@Data
@ToString
@Document("commonPlaylist")
public class CommonPlaylistDocument {

    @Id
    public String id;

    private String playlistName;

    private String playlistDescription;

    private String playlistImageId;

    private boolean premium;

    private HashSet<String> songIds;
}
