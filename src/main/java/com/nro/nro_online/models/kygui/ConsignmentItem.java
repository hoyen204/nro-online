package com.nro.nro_online.models.kygui;

import lombok.Getter;
import lombok.Setter;
import nro.models.item.Item;

/**
 * Debug Arriety
 */
@Setter
@Getter
public class ConsignmentItem extends Item {

    private int consignID;
    private long consignorID;
    private int priceGold;
    private int priceGem;
    private byte tab;
    private boolean sold;
    private boolean upTop;
    private long timeConsign;
    private long playerSell;
}
