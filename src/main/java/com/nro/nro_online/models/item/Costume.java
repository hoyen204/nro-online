package com.nro.nro_online.models.item;

import lombok.Getter;

public class Costume {

    public int tempId;
    @Getter
    public int[] id;

    public Costume(int tempId, int... id) {
        this.tempId = tempId;
        this.id = id;
    }
}
