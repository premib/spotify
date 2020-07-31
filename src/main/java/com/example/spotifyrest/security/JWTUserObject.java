package com.example.spotifyrest.security;

import com.example.spotifyrest.model.AccountsTable;
import com.example.spotifyrest.repository.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JWTUserObject implements UserDetailsService {

  @Autowired
  private AccountsRepository accountsRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    final AccountsTable requestedAccount = accountsRepository.findByEmail(username);

    if (requestedAccount == null) {
      throw new UsernameNotFoundException(username + " NOT FOUND");
    }

    return org.springframework.security.core.userdetails.User
        .withUsername(username)
        .password(requestedAccount.getPassword())
        .authorities(requestedAccount.getRole())
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(false)
        .build();
  }

}
