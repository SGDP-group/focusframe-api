package com.focusframe.focusframe_api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth_tokens")
public class AuthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false, unique = true)
    private String token;

    public AuthToken() {}

    public AuthToken(String ipAddress, String token) {
        this.ipAddress = ipAddress;
        this.token = token;
    }
}
