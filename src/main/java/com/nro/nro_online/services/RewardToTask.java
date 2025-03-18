/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.services;

import com.nro.nro_online.consts.ConstTask;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.utils.Util;

/**
 *
 * @author Arriety
 */
public class RewardToTask {

    private static RewardToTask instance;

    public static RewardToTask getInstance() {
        if (instance == null) {
            instance = new RewardToTask();
        }
        return instance;
    }

    public void rewardToTask(Player player) {
        int goldReward = 0;
        int rubyReward = 0;
        switch (player.playerTask.sideTask.level) {
            case ConstTask.EASY:
                goldReward = ConstTask.GOLD_EASY;
                rubyReward = ConstTask.RUBY_EASY;
                break;
            case ConstTask.NORMAL:
                goldReward = ConstTask.GOLD_NORMAL;
                rubyReward = ConstTask.RUBY_NORMAL;
                break;
            case ConstTask.HARD:
                goldReward = ConstTask.GOLD_HARD;
                rubyReward = ConstTask.RUBY_HARD;
                break;
            case ConstTask.VERY_HARD:
                goldReward = ConstTask.GOLD_VERY_HARD;
                rubyReward = ConstTask.RUBY_VERY_HARD;
                break;
            case ConstTask.HELL:
                goldReward = ConstTask.GOLD_HELL;
                rubyReward = ConstTask.RUBY_HELL;
                break;
        }
        if (Util.isTrue(20, 100)) {
            Item nro = ItemService.gI().createNewItem((short) Util.nextInt(2045, 2051));
            InventoryService.gI().addItemBag(player, nro, 9999);
        }
        Item thoivang = ItemService.gI().createNewItem((short) 457);
        thoivang.quantity = goldReward;
        InventoryService.gI().addItemBag(player, thoivang, 9999);
        player.inventory.addRuby(rubyReward);
        Service.getInstance().sendMoney(player);
        InventoryService.gI().sendItemBags(player);
        Service.getInstance().sendThongBao(player, "Bạn nhận được "
                + Util.numberToMoney(goldReward)
                + " vàng và " + Util.numberToMoney(rubyReward) + " hồng ngọc");
    }
}
