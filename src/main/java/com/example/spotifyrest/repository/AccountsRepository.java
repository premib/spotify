package com.example.spotifyrest.repository;

import com.example.spotifyrest.model.AccountsTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountsRepository  extends JpaRepository<AccountsTable, String> {

    public boolean existsByEmail(String email);

    public AccountsTable findByEmail(String email);


}
