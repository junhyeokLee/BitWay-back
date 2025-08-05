package com.example.bitway_back.domain.coin;

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

    @Column(name = "symbol_name", nullable = true, columnDefinition = "varchar(255) default ''")
    private String symbolName;
}
