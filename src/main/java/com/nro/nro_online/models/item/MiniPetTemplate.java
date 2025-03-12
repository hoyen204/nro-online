package com.nro.nro_online.models.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class MiniPetTemplate {
    private int id;
    private short head;
    private short body;
    private short leg;
}