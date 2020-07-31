package com.example.spotifyrest.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;


@Data
@ToString
@NoArgsConstructor
public class UserLogin {

    @NonNull
    private String email;

    @NonNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY, required = true)
    private String password;
}
