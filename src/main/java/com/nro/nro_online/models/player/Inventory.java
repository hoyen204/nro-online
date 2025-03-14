package com.nro.nro_online.models.player;

import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.models.item.Item;
import lombok.Getter;
import nro.models.item.Item;

/**
 *
 * Arriety
 *
 */
public class Inventory {

    public static final int LIMIT_GOLD = 2_000_000_000;

    public static final int LIMIT_RUBY = 2000000000;

    private Player player;

    public Item trainArmor;

    public List<Item> itemsBody;
    public List<Item> itemsBag;
    public List<Item> itemsBox;

    public List<Item> itemsBoxCrackBall;

    @Getter
    public long gold, goldLimit;
    @Getter
    public int gem;
    @Getter
    public int ruby;

    public Inventory(Player player) {
        this.player = player;
        itemsBody = new ArrayList<>();
        itemsBag = new ArrayList<>();
        itemsBox = new ArrayList<>();
        itemsBoxCrackBall = new ArrayList<>();
    }

    public int getQuantity(int tempId) {
        Item item = itemsBag.stream()
                .filter(i -> i.template.id == tempId)
                .findFirst()
                .orElse(null);
        return item != null ? item.quantity : 0;
    }

    public long getGoldLimit() {
        return goldLimit + LIMIT_GOLD;
    }

    public int getRubyLimit() {
        return ruby + 2000000000;
    }

    public long getGoldDisplay() {
        long amount = gold;
        if (amount > Integer.MAX_VALUE && !player.isVersionAbove(214)) {
            return Integer.MAX_VALUE;
        }
        return amount;
    }

    public void subGem(int num) {
        this.gem -= num;
    }

    public void subGold(int num) {
        this.gold -= num;
    }

    public void subRuby(int num) {
        this.ruby -= num;
    }

    public void addGold(int gold) {
        this.gold += gold;
        long goldLimit = getGoldLimit();
        if (this.gold > goldLimit) {
            this.gold = goldLimit;
        }
    }

    public void addRuby(int ruby) {
        this.ruby += ruby;
        int rupilimit = getRubyLimit();
        if (this.ruby > rupilimit) {
            this.ruby = rupilimit;
        }
    }

    public void dispose() {
        this.player = null;
        if (this.trainArmor != null) {
            this.trainArmor.dispose();
        }
        this.trainArmor = null;
        if (this.itemsBody != null) {
            for (Item it : this.itemsBody) {
                it.dispose();
            }
            this.itemsBody.clear();
        }
        if (this.itemsBag != null) {
            for (Item it : this.itemsBag) {
                it.dispose();
            }
            this.itemsBag.clear();
        }
        if (this.itemsBox != null) {
            for (Item it : this.itemsBox) {
                it.dispose();
            }
            this.itemsBox.clear();
        }
        if (this.itemsBoxCrackBall != null) {
            for (Item it : this.itemsBoxCrackBall) {
                it.dispose();
            }
            this.itemsBoxCrackBall.clear();
        }
        this.itemsBody = null;
        this.itemsBag = null;
        this.itemsBox = null;
        this.itemsBoxCrackBall = null;
    }
}
