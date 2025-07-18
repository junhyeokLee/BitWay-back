//package com.example.bitway_back.domain;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//@Getter @Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Table(name = "favorite_coin")
//public class FavoriteCoin {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String coinCode; // 예: KRW-BTC
//
//    private String coinName;
//
//    @Builder.Default
//    @Column(nullable = false)
//    private boolean enabled = true;
//
//    @Column
//    private Boolean alertEnabled;
//
//    @Column
//    private Double alertPrice;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")  // FK
//    private User user;
//}
