package com.focusframe.focusframe_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "auth_tokens")
public class AuthToken {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String ipAddress;

    @Getter
    @Setter
    @Column(nullable = false, unique = true)
    private String token;

    public Boolean getExpired() {
        return isExpired;
    }

    public void setExpired(Boolean expired) {
        isExpired = expired;
    }

    @Column(nullable = false)
    private Boolean isExpired = false;

    public AuthToken() {}

    public AuthToken(String ipAddress, String token) {
        this.ipAddress = ipAddress;
        this.token = token;
    }
}
