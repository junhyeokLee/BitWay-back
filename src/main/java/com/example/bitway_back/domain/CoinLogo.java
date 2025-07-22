package com.example.bitway_back.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;


@Entity
@Table(name = "coin_logos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoinLogo {
    @Id
    private String symbol;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @ColumnDefault("")
    @Column(nullable = true,name = "symbol_name")
    private String symbolName;
}
