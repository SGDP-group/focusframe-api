package com.focusframe.focusframe_api.repository;

import com.focusframe.focusframe_api.model.AuthToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<AuthToken, Long> {
    Optional<AuthToken> findByToken(String token);
    Optional<AuthToken> findByIpAddress(String ipAddress);

    @Modifying
    @Transactional
    @Query(value = "UPDATE auth_tokens SET expired_status = 1 WHERE token = :token", nativeQuery = true)
    void setAuthAsExpired(@Param("token") String token);
}