package com.example.bitway_back.domain.coin;

import com.example.bitway_back.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "favorite_coin")
public class FavoriteCoin {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Optional but explicit
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String symbol; // 예: BTC, ETH

    @Column(nullable = false)
    private String symbolName; // 예: 비트코인, 이더리움

    @Column(name = "alert_enabled")
    private Boolean alertEnabled;

}
