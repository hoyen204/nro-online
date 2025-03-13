package com.nro.nro_online.models.mob;

import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.models.item.ItemReward;

public class MobReward {

    public int tempId;
    public List<ItemReward> itemRewards;
    public List<ItemReward> goldRewards;
    public List<ItemReward> capsuleKyBi;
    public List<ItemReward> foods;
    public List<ItemReward> biKieps;

    public MobReward() {
        this.itemRewards = new ArrayList<>();
        this.goldRewards = new ArrayList<>();
        this.capsuleKyBi = new ArrayList<>();
        this.foods = new ArrayList<>();
        this.biKieps = new ArrayList<>();
    }
}
