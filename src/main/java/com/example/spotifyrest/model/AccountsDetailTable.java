package com.example.spotifyrest.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@ToString
@Table(name = "accounts_detail")
public class AccountsDetailTable {

    @Id
    @Column(name = "email")
    private String email;

    @Column(nullable = false)
    private String userName;

    private Date dob;

    private String gender;

    @Column(columnDefinition = "bit default 0")
    private boolean shareData;

    @Column(unique = true, nullable = false)
    private String customPlaylistMongoId;
}
