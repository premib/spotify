package com.example.spotifyrest.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
public class InsertionResponse {

    List<String> added = new ArrayList<>();
    List<String> ignored = new ArrayList<>();

    public InsertionResponse(List<String> added, List<String> ignored){
        this.added = added;
        this.ignored = ignored;
    }
}
