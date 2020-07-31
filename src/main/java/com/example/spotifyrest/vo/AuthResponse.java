package com.example.spotifyrest.vo;


import lombok.Data;
import lombok.ToString;

//@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class AuthResponse {

    String response;

    public AuthResponse(String response){

        this.response = response;
    }
}
