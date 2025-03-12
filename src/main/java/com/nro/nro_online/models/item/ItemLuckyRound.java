package com.nro.nro_online.models.item;

import java.util.ArrayList;
import java.util.List;

public class ItemLuckyRound {

    public ItemTemplate temp;
    public double percent;
    public List<ItemOptionLuckyRound> itemOptions;

    public ItemLuckyRound() {
        this.itemOptions = new ArrayList<>();
    }
}