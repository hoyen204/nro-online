package com.nro.nro_online.entities;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class ItemOptionEntity {
    private int id;
    private int param;
    @SerializedName("active_card")
    private int activeCard;
}
