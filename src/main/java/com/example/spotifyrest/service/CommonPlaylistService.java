package com.example.spotifyrest.service;

import com.example.spotifyrest.exception.ServiceException;
import com.example.spotifyrest.model.CommonPlaylistDocument;
import com.example.spotifyrest.repository.CommonPlaylistRepository;
import com.example.spotifyrest.vo.Playlist;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Objects;

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

    @Override
    public String createCommonPlaylist(Playlist newPlaylist, MultipartFile imageFile) throws IOException {

        System.out.println(newPlaylist);
        if(newPlaylist.getPlaylistName() != null){
            if(commonPlaylistRepository.existsByPlaylistName(newPlaylist.getPlaylistName())){
                throw new ServiceException("Playlist already exists", HttpStatus.CONFLICT);
            }

            CommonPlaylistDocument newPlaylistDocument = modelMapper.map(newPlaylist, CommonPlaylistDocument.class);
            System.out.println(newPlaylistDocument);
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
        else {
            throw new ServiceException("playlist name cannot be null", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public GridFsResource getImage(){
        Query query = new Query();
        String id = "5f24081a1ba530101f6c538a";
        query.addCriteria(Criteria.where("_id").is(id));
        GridFSFile gridFsFile = gridFsTemplate.findOne(query);
        StringWriter st = new StringWriter();
        try {
            byte[] byteArrayOutputStream = gridFsOperations.getResource(gridFsFile).getInputStream().readAllBytes();
            System.out.println(byteArrayOutputStream);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return gridFsOperations.getResource(gridFsFile);
    }
}
