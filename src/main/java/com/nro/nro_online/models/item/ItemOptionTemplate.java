package com.nro.nro_online.models.item;

public class ItemOptionTemplate {

    public int id;

    public String name;

    public int type;

    public ItemOptionTemplate() {
    }

    public ItemOptionTemplate(int id, String name, int type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
}