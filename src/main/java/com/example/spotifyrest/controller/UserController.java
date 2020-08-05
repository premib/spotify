package com.example.spotifyrest.controller;

import com.example.spotifyrest.exception.ServiceException;
import com.example.spotifyrest.service.SongServiceable;
import com.example.spotifyrest.service.UserServiceable;
import com.example.spotifyrest.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping(value = "v1/user/")
public class UserController {

    @Autowired
    private UserServiceable userServiceable;

    @Autowired
    private SongServiceable songServiceable;

    @PostMapping(value = "register")
    public ResponseEntity<AuthResponse> accountRegistration(@RequestBody Register credentials){
        return new ResponseEntity<>(userServiceable.registerNewUser(credentials), HttpStatus.OK);
    }

    @PostMapping(value = "login")
    public ResponseEntity<AuthResponse> accountLogin(@RequestBody UserLogin credentials){
        return new ResponseEntity<>(userServiceable.login(credentials), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_PREMIUM')")
    @PostMapping(value = "cpl/create/{playlistName}")
    public ResponseEntity<String> cplCreation(@PathVariable String playlistName){

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return new ResponseEntity<>(userServiceable.createCustomPlaylist(playlistName, authentication.getName()), HttpStatus.OK);
        }
        catch (AuthenticationException exception){
            throw new ServiceException("Not a registered user/ Token expired, login again", HttpStatus.UNAUTHORIZED);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_PREMIUM', 'ROLE_ADMIN')")
    @PostMapping(value = "cpl/add_songs")
    public ResponseEntity<AuthResponse> cplSongAddition(@RequestBody AddSongToCPL songAddition){

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return new ResponseEntity<>(userServiceable.addSongsToCustomPlaylist(songAddition.getPlaylistName(), authentication.getName(), songAddition.getSongId()), HttpStatus.OK);
        }
        catch (AuthenticationException exception){
            throw new ServiceException("Not a registered user/ Token expired, login again", HttpStatus.UNAUTHORIZED);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_PREMIUM', 'ROLE_ADMIN')")
    @DeleteMapping(value = "cpl/delete_song/{playlistName}")
    public ResponseEntity<ListSong> cplSongDeletion(@PathVariable String playlistName, @RequestBody ListSong songDeletion){

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return new ResponseEntity<>(userServiceable.deleteSongFromCustomPlaylist(playlistName, authentication.getName(), songDeletion), HttpStatus.OK);
        }
        catch (AuthenticationException e) {
            throw new ServiceException("Not a registered user/ Token expired, login again" + e, HttpStatus.UNAUTHORIZED);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_PREMIUM', 'ROLE_ADMIN')")
    @DeleteMapping(value = "cpl/{playlistName}")
    public ResponseEntity<String> cplDeletion(@PathVariable String playlistName){

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return new ResponseEntity<>(userServiceable.deleteCustomPlaylist(playlistName, authentication.getName()), HttpStatus.OK);
        }
        catch (AuthenticationException e) {
            throw new ServiceException("Not a registered user/ Token expired, login again" + e, HttpStatus.UNAUTHORIZED);
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "upgrade")
    public ResponseEntity<AuthResponse> userUpgradation(){

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return new ResponseEntity<>(userServiceable.upgradeToPremium(authentication.getName()), HttpStatus.OK);
        }
        catch (AuthenticationException e){
            throw new ServiceException("Not a registered user/ Token expired, login again" + e, HttpStatus.UNAUTHORIZED);
        }
    }

    @PreAuthorize("hasRole('ROLE_PREMIUM')")
    @PutMapping(value = "downgrade")
    public ResponseEntity<AuthResponse> userDowngradation(){

        try{
            Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
            return new ResponseEntity<>(userServiceable.downgradeToUser(authentication.getName()), HttpStatus.OK);
        }
        catch (AuthenticationException e){
            throw new ServiceException("Not a registered user/ Token expired, login again" + e, HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping(value = "artist/{artistId}")
    public ResponseEntity<String> userFollowsArtist(@PathVariable String artistId){

        try{
            Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
            return new ResponseEntity<>(userServiceable.followArtist(artistId, authentication.getName()), HttpStatus.OK);
        }
        catch (AuthenticationException e){
            throw new ServiceException("Not a registered user/ Token expired, login again" + e, HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping(value = "artist/{artistId}")
    public ResponseEntity<String> userUnFollowArtist(@PathVariable String artistId){

        try{
            Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
            return new ResponseEntity<>(userServiceable.unFollowArtist(artistId, authentication.getName()), HttpStatus.OK);
        }
        catch (AuthenticationException e){
            throw new ServiceException("Not a registered user/ Token expired, login again" + e, HttpStatus.UNAUTHORIZED);
        }
    }
}
