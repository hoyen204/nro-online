package com.nro.nro_online.card;

import com.google.gson.annotations.SerializedName;
import com.nro.nro_online.utils.Log;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Card {

    @SerializedName("id")
    private int id;

    @SerializedName("amount")
    private int amount;

    @SerializedName("level")
    private int level;

    @SerializedName("use")
    private boolean isUse;

    private transient CardTemplate cardTemplate;

    public void addAmount(int amount) {
        if (amount < 0) return; // Tránh âm, không thì toang 😂
        this.amount += amount;
        if (cardTemplate != null && this.amount >= cardTemplate.getMaxAmount()) {
            levelUp();
        }
    }

    private void levelUp() {
        this.amount = 0;
        this.level++;
    }

    public void setTemplate() {
        this.cardTemplate = CardManager.gI().find(id);
        if (this.cardTemplate == null) {
            Log.error("CardTemplate not found for id: " + id);
        }
    }
}