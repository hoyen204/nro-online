/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nro.nro_online.services.func.lr;

import java.util.List;

import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.RewardService;
import com.nro.nro_online.services.Service;

/**
 *
 * @Build Arriety
 */
public class LuckyRoundGold extends AbsLuckyRound {

    private static LuckyRoundGold i;

    public static LuckyRoundGold gI() {
        if (i == null) {
            i = new LuckyRoundGold();
        }
        return i;
    }

    private LuckyRoundGold() {
        this.price = 50;
        this.ticket = 821;
        this.icons.add(419);
        this.icons.add(420);
        this.icons.add(421);
        this.icons.add(422);
        this.icons.add(423);
        this.icons.add(424);
        this.icons.add(425);
    }

    @Override
    public List<Item> reward(Player player, byte quantity) {
        List<Item> list = RewardService.gI().getListItemLuckyRound(player, quantity);
        addItemToBox(player, list);
        return list;
    }

    @Override
    public boolean checkMoney(Player player, int price) {
        if (player.inventory.getRuby() < price) {
            Service.getInstance().sendThongBao(player, "Bạn không đủ ngọc hồng");
            return false;
        }
        return true;
    }

    @Override
    public void payWithMoney(Player player, int price) {
        player.inventory.subRuby(price);
    }

}
