package com.beom.api.totp.demo.dal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity class that represents a user
 *
 * @author beom
 * @since 2024/03/16
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "'user'")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String email;
    private String password;

    // Establishing one-to-many relationship with MfaInfo
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MfaInfo> mfaInfoList;
}

