package com.example.spotifyrest.service;

import com.example.spotifyrest.exception.ServiceException;
import com.example.spotifyrest.model.AccountsDetailTable;
import com.example.spotifyrest.model.AccountsTable;
import com.example.spotifyrest.model.CustomPlaylistDocument;
import com.example.spotifyrest.model.Role;
import com.example.spotifyrest.repository.AccountsDetailRepository;
import com.example.spotifyrest.repository.AccountsRepository;
import com.example.spotifyrest.repository.CustomPlaylistRepository;
import com.example.spotifyrest.repository.SongsRepository;
import com.example.spotifyrest.security.JWTProvider;
import com.example.spotifyrest.vo.AuthResponse;
import com.example.spotifyrest.vo.ListSong;
import com.example.spotifyrest.vo.Register;
import com.example.spotifyrest.vo.UserLogin;
import com.mongodb.client.result.UpdateResult;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class UserService implements UserServiceable{

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTProvider jwtProvider;

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private AccountsDetailRepository accountsDetailRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CustomPlaylistRepository customPlaylistRepository;

    @Autowired
    private SongsRepository songsRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    public UserService() {
    }

    @Override
    public AuthResponse registerNewUser(Register newUser) {

        if(!accountsRepository.existsByEmail(newUser.getEmail()) && !accountsDetailRepository.existsById(newUser.getEmail())){

            String customPlaylistMongoId = createNewPlaylistMongoId(newUser.getEmail(), true);

            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            AccountsTable newUserAccount = modelMapper.map(newUser, AccountsTable.class);
            AccountsDetailTable newUserAccountDetail = modelMapper.map(newUser, AccountsDetailTable.class);
            newUserAccountDetail.setCustomPlaylistMongoId(customPlaylistMongoId);

            accountsRepository.save(newUserAccount);
            accountsDetailRepository.save(newUserAccountDetail);

            String token;
            if(newUser.isPremiumUser()){
                token = jwtProvider.createToken(newUser.getEmail(), Arrays.asList(Role.ROLE_PREMIUM, Role.ROLE_USER));
            }
            else{
                token = jwtProvider.createToken(newUser.getEmail(), Arrays.asList(Role.ROLE_USER));
            }

            return new AuthResponse(token);
        }
        else{
            throw new ServiceException("User with email already exists!", HttpStatus.CONFLICT);
        }
    }

    @Override
    public AuthResponse login(UserLogin credential) {

        try{

            String email = credential.getEmail();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, credential.getPassword()));

            return new AuthResponse(jwtProvider.createToken(email, accountsRepository.findByEmail(email).getRole()));
        } catch (AuthenticationException e) {
            throw new ServiceException("Invalid email/ password", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public String createCustomPlaylist(String name, String email) {

        try{

            Query query = new Query();
            String userCustomPlaylistId = accountsDetailRepository.findById(email).get().getCustomPlaylistMongoId();

            if(!customPlaylistRepository.existsById(userCustomPlaylistId)){
                userCustomPlaylistId = createNewPlaylistMongoId(email, false);
            }
            query.addCriteria(Criteria.where("id").is(userCustomPlaylistId));
            CustomPlaylistDocument userCustomPlaylist = mongoTemplate.findOne(query, CustomPlaylistDocument.class);
            if(userCustomPlaylist != null){
                if(userCustomPlaylist.getPlaylists() == null){
                    userCustomPlaylist.setPlaylists(new HashMap<>());
                }
                if(userCustomPlaylist.getPlaylists().containsKey(name)){
                    throw new ServiceException("Playlist already exists", HttpStatus.CONFLICT);
                }
                else{

                    userCustomPlaylist.getPlaylists().put(name, new String[]{});
                    customPlaylistRepository.save(userCustomPlaylist);
                }
            }
            return "Successfully created playlist: "+name+" -"+accountsDetailRepository.findByEmail(email);
        }
        catch (Exception e){
            throw new ServiceException(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }

    }

    @Override
    public AuthResponse addSongsToCustomPlaylist(String playlistName, String email, List<String> songsMongoId) {

        List<String> wrongSongId = new ArrayList<>();
        String userCustomPlaylistMongoId = accountsDetailRepository.findByEmail(email).getCustomPlaylistMongoId();

        if(!customPlaylistRepository.existsById(userCustomPlaylistMongoId)) {
            userCustomPlaylistMongoId = createNewPlaylistMongoId(email, false);
        }

        Query query = new Query();
        Update update = new Update();

        query.addCriteria(Criteria.where("id").is(userCustomPlaylistMongoId));

        for (String songId: songsMongoId) {
            if(songsRepository.existsById(songId)){
                update.addToSet("playlists."+playlistName , songId);
                UpdateResult updateResult = mongoTemplate.updateFirst(query, update, CustomPlaylistDocument.class);
                if(updateResult.getModifiedCount() == 0)
                    wrongSongId.add(songId);
            }
            else{
                wrongSongId.add(songId);
            }
        }

        if(wrongSongId.size() > 0)
            return new AuthResponse("done, but ignored musicIds -"+wrongSongId.toString());

        return new AuthResponse("done");
    }

    @Override
    public ListSong deleteSongFromCustomPlaylist(String playlistName, String email, ListSong songIds){

        Optional<AccountsDetailTable> accountsDetailTable = accountsDetailRepository.findById(email);

        if(accountsDetailTable.isPresent()){
            Optional<CustomPlaylistDocument> userCustomPlaylist = customPlaylistRepository
                    .findById(accountsDetailTable.get().getCustomPlaylistMongoId());

            if(userCustomPlaylist.isPresent()){
                if(userCustomPlaylist.get().getPlaylists().containsKey(playlistName)){
                    List<String> songsAvailable = new LinkedList<>(Arrays.asList(userCustomPlaylist.get().getPlaylists().get(playlistName)));

                    for(String songId : songIds.getSongIds()){
                        if(songsAvailable.remove(songId)){
                            songIds.getDeleted().add(songId);
                        }
                        else{
                            songIds.getIgnored().add(songId);
                        }
                    }

                    userCustomPlaylist.get().getPlaylists().put(playlistName, songsAvailable.toArray(new String[0]));
                    customPlaylistRepository.save(userCustomPlaylist.get());

                    songIds.setSongIds(null);
                    return songIds;
                }
                else{
                    throw new ServiceException("Playlist with enter name not found!!", HttpStatus.BAD_REQUEST);
                }
            }
            else{
                createNewPlaylistMongoId(email, false);
                return deleteSongFromCustomPlaylist(playlistName, email, songIds);
            }
        }
        else{
            throw new ServiceException("Read account details", HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @Override
    public String deleteCustomPlaylist(String playlistName, String email) {

        Optional<AccountsDetailTable> accountsDetailTable = accountsDetailRepository.findById(email);

        if(accountsDetailTable.isPresent()){
            String userCustomPlaylistId = accountsDetailTable.get().getCustomPlaylistMongoId();
            Optional<CustomPlaylistDocument> oldCustomPlaylist = customPlaylistRepository.findById(userCustomPlaylistId);

            if(oldCustomPlaylist.isPresent()){
                if(oldCustomPlaylist.get().getPlaylists() != null){
                    return "Deleted- "+Arrays.toString((oldCustomPlaylist.get().getPlaylists().remove(playlistName)));
                }
                else{
                    throw new ServiceException(playlistName+" is not a valid custom playlist", HttpStatus.BAD_REQUEST);
                }
            }
            else{
                createNewPlaylistMongoId(email, false);
                return deleteCustomPlaylist(playlistName, email);
            }
        }
        else{
            throw new ServiceException("Edit personal details", HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @Override
    public AuthResponse upgradeToPremium(String email){
        try{
            Optional<AccountsTable> userCredentials = accountsRepository.findById(email);
            AccountsTable savedAccount;

            if(userCredentials.isPresent()){
                if(userCredentials.get().getRole().contains(Role.ROLE_PREMIUM)){
                    throw new ServiceException("You are already a premium user", HttpStatus.BAD_REQUEST);
                }

                userCredentials.get().getRole().add(Role.ROLE_PREMIUM);
                savedAccount = accountsRepository.saveAndFlush(userCredentials.get());
            }
            else{
                throw new ServiceException("Unauthorized user", HttpStatus.UNAUTHORIZED);
            }

            return new AuthResponse(jwtProvider.createToken(email, savedAccount.getRole()));
        }
        catch (Exception e){
            System.out.println(e);
            throw new ServiceException("Unknown exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public AuthResponse downgradeToUser(String email){
        try{
            Optional<AccountsTable> userCredentials = accountsRepository.findById(email);
            AccountsTable savedAccount;

            if(userCredentials.isPresent()){
                if(userCredentials.get().getRole().contains(Role.ROLE_PREMIUM)){

                    userCredentials.get().getRole().remove(Role.ROLE_PREMIUM);
                    savedAccount = accountsRepository.saveAndFlush(userCredentials.get());
                    return new AuthResponse(jwtProvider.createToken(email, savedAccount.getRole()));
                }
                else{
                    throw new ServiceException("Only premium users can be downgraded", HttpStatus.FORBIDDEN);
                }
            }
            else{
                throw new ServiceException("Unauthorized user", HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception exception) {
            System.out.println(exception);
            throw new ServiceException("Unknown error, try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String createNewPlaylistMongoId(String email, boolean firstTimeCreation){

        CustomPlaylistDocument retrievedCustomPlaylistDocument = customPlaylistRepository.save(new CustomPlaylistDocument());

        if(!firstTimeCreation){
            AccountsDetailTable detailTable = accountsDetailRepository.findByEmail(email);
            detailTable.setCustomPlaylistMongoId(retrievedCustomPlaylistDocument.getId());
            accountsDetailRepository.save(detailTable);
        }
        return  retrievedCustomPlaylistDocument.getId();
    }

}
