package com.alltime.identity.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_token_value", columnList = "token", unique = true)
})
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false, unique = true, length = 100)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    public Boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }

}
