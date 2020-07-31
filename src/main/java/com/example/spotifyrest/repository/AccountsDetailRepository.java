package com.example.spotifyrest.repository;

import com.example.spotifyrest.model.AccountsDetailTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountsDetailRepository extends JpaRepository<AccountsDetailTable, String> {

    boolean existsByEmail(String email);

    AccountsDetailTable getOne(String email);

    AccountsDetailTable findByEmail(String email);

}
