package com.example.spotifyrest.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.HashMap;

@Entity
@Document("customPlaylist")
@Data
@ToString
public class CustomPlaylistDocument{

    @Id
    private String id;

    private HashMap<String, String[]> playlists;

}
