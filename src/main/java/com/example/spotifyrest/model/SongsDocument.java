package com.example.spotifyrest.model;


import lombok.Data;
import lombok.ToString;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Document(value = "songs")
@Data
@ToString
public class SongsDocument {

    @Id
    private String id;

    private String songName;

    @Indexed(direction = IndexDirection.ASCENDING)
    private String artistName;

    private String songType;

    private Binary songBytes;
}
