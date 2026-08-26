package com.clinic.clinic.repository;

import com.clinic.clinic.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    Optional<PasswordReset> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE PasswordReset p SET p.usedAt = :now WHERE p.user.id = :userId AND p.usedAt IS NULL")
    void invalidateActiveTokens(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
