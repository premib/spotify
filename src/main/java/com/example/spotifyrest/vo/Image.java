package com.example.spotifyrest.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Image {

    private String imageName;
    private String imageType;
    private String imageBase64String;
}
