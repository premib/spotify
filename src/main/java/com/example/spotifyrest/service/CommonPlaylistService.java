package com.example.spotifyrest.service;

import com.example.spotifyrest.exception.ServiceException;
import com.example.spotifyrest.model.CommonPlaylistDocument;
import com.example.spotifyrest.model.Role;
import com.example.spotifyrest.repository.CommonPlaylistRepository;
import com.example.spotifyrest.repository.SongsRepository;
import com.example.spotifyrest.vo.*;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class CommonPlaylistService implements CommonPlaylistServiceable{

    @Autowired
    CommonPlaylistRepository commonPlaylistRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFsOperations gridFsOperations;

    @Autowired
    SongServiceable songServiceable;

    @Autowired
    SongsRepository songsRepository;

    @Override
    public String createCommonPlaylist(Playlist newPlaylist, MultipartFile imageFile) throws IOException {

        System.out.println(newPlaylist);
        if(newPlaylist.getPlaylistName() != null){
            if(commonPlaylistRepository.existsByPlaylistName(newPlaylist.getPlaylistName()))
                throw new ServiceException("Playlist already exists", HttpStatus.CONFLICT);

            CommonPlaylistDocument newPlaylistDocument = modelMapper.map(newPlaylist, CommonPlaylistDocument.class);
            newPlaylistDocument.setSongIds(new HashSet<>());
            InputStream imageInputStream = new ByteArrayInputStream(imageFile.getBytes());
            System.out.println(imageInputStream);
            ObjectId imageId = gridFsTemplate.store(imageInputStream,
                    newPlaylist.getPlaylistName()+"."+ Objects.requireNonNull(imageFile.getContentType())
                            .strip()
                            .split("/")[1],
                    imageFile.getContentType());
            newPlaylistDocument.setPlaylistImageId(imageId.toString());

            return commonPlaylistRepository.save(newPlaylistDocument).getId();
        }
        else
            throw new ServiceException("playlist name cannot be null", HttpStatus.BAD_REQUEST);

    }

    public Image getImage(String imageId){

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(imageId));
        GridFSFile imageFile = gridFsTemplate.findOne(query);
        GridFsResource imageResource = gridFsOperations.getResource(Objects.requireNonNull(imageFile));

        try {
            Image image = new Image();
            String imageString = Base64.getEncoder().encodeToString(imageResource.getInputStream().readAllBytes());
            image.setImageBase64String(imageString);
            image.setImageName(imageFile.getFilename());
            image.setImageType((imageResource.getContentType() == null) ?
                    "image/"+imageFile.getFilename().strip().split("\\.")[1]:
                    imageResource.getContentType());
            return image;
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new ServiceException("IO Error"+exception, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Playlist getPlaylist(String playlistId){
        Optional<CommonPlaylistDocument> retrievedPlaylist;
        Playlist playlist;

        if(commonPlaylistRepository.existsById(playlistId)){
            retrievedPlaylist = commonPlaylistRepository.findById(playlistId);
            playlist = modelMapper.map(retrievedPlaylist.get(), Playlist.class);

            if(playlist.isPremium()){
                if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(Role.ROLE_PREMIUM)){
                    modelMapper.map(getImage(retrievedPlaylist.get().getPlaylistImageId()), playlist);
                    return playlist;
                }
                else
                    throw new ServiceException("playlist for premium users", HttpStatus.UNAUTHORIZED);

            }
            modelMapper.map(getImage(retrievedPlaylist.get().getPlaylistImageId()), playlist);
            return playlist;
        }
        else
            throw new ServiceException("playlist doesn't exist", HttpStatus.BAD_REQUEST);

    }

    @Override
    public Playlist getPlaylistByName(String playlistName){
        CommonPlaylistDocument retrievedPlaylist;
        Playlist playlist;

        if(commonPlaylistRepository.existsByPlaylistName(playlistName)){
            retrievedPlaylist = commonPlaylistRepository.findByPlaylistName(playlistName);
            playlist = modelMapper.map(retrievedPlaylist, Playlist.class);
            playlist = modelMapper.map(getImage(retrievedPlaylist.getPlaylistImageId()), Playlist.class);
            return playlist;
        }
        else
            throw new ServiceException("playlist doesn't exist", HttpStatus.BAD_REQUEST);

    }

    @Override
    public InsertionResponse addSongToPlaylist(String playlistId, ListSong songs){
        System.out.println(songs);
        Optional<CommonPlaylistDocument> playlist;
        InsertionResponse response = new InsertionResponse();

        if(commonPlaylistRepository.existsById(playlistId))
            playlist = commonPlaylistRepository.findById(playlistId);
        else
            throw new ServiceException("playlist doesn't exist", HttpStatus.BAD_REQUEST);


        for(String song: songs.getSongIds()){
            if(songsRepository.existsById(song)){
                if(playlist.get().getSongIds().add(song))
                    response.getAdded().add(song);
            }
            else
                response.getIgnored().add(song);

        }

        commonPlaylistRepository.save(playlist.get());
        return response;
    }

    @Override
    public List<Song> getSongDetails(String playlistId){

        if(commonPlaylistRepository.existsById(playlistId)){
            HashSet<String> songIds = commonPlaylistRepository.findById(playlistId).get().getSongIds();
            ListSong listSong  = new ListSong();
            listSong.setSongIds(new ArrayList<>(songIds));
            return songServiceable.getSongDetails(listSong);
        }
        else
            throw new ServiceException("playlist doesn't exist", HttpStatus.BAD_REQUEST);

    }

    @Override
    public String deletePlaylist(String playlistId) {
        if(playlistId == null)
            throw new ServiceException("playlistId cannot by null", HttpStatus.BAD_REQUEST);

        if(!commonPlaylistRepository.existsById(playlistId))
            throw new ServiceException("playlist doesn't exist", HttpStatus.BAD_REQUEST);

        commonPlaylistRepository.deleteById(playlistId);
        return "deleted "+playlistId;
    }
}
