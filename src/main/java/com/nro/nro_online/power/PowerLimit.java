package com.nro.nro_online.power;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class PowerLimit {

    private int id;
    private long power;
    private int hp;
    private int mp;
    private int damage;
    private int defense;
    private int critical;
}
