package com.example.spotifyrest.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@Entity
@Document("artist")
public class ArtistDocument {

    @Id
    private String id;

    private String artistName;

    private String artistDescription = "artist description";

    private ArrayList<String> relatedPlaylist;

    private String artistImage;
}
