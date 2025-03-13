package com.nro.nro_online.card;

import java.util.List;

import com.nro.nro_online.models.item.ItemOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardTemplate {

    private int id;
    private int itemID;
    private byte rank;
    private byte maxAmount;
    private byte type;
    private int icon;
    private String name;
    private String info;
    private short mobID;
    private short head;
    private short body;
    private short leg;
    private short bag;
    private short aura;
    private List<ItemOption> options;
}
