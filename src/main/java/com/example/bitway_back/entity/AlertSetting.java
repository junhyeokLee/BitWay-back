package com.example.bitway_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "alert_setting")
public class AlertSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String coinCode; // 예: KRW-BTC

    private Double targetPrice;

    private boolean above; // true: 위로 돌파, false: 아래로 하락

    private boolean enabled = true;
}