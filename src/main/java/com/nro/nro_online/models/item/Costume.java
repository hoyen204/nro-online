package com.nro.nro_online.models.item;

public class Costume {

    public int tempId;
    public int[] id;

    public Costume(int tempId, int... id) {
        this.tempId = tempId;
        this.id = id;
    }

    public int[] getID() {
        return id;
    }
}
