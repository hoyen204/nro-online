/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.player;

import nro.manager.TopWhis;
import nro.services.Service;

/**
 *
 * @author Arrriety
 */
public class UpdateEffChar {

    private static UpdateEffChar i;

    public static UpdateEffChar getInstance() {
        if (i == null) {
            i = new UpdateEffChar();
        }
        return i;
    }

    public void updateEff(Player player) {
        try {
            if (player.isPl()) {
                int playerIdInt = (int) player.id;
                if (TopWhis.TOP_ONE == player.id) {
                    Service.getInstance().addEffectChar(player, 58, 1, -1, -1, -1);
//                    System.out.println(player.id);
                }
                if (TopWhis.TOP_THREE == player.id) {
                    Service.getInstance().addEffectChar(player, 57, 1, -1, -1, -1);
//                    System.out.println(player.id);
                }
                if (TopWhis.TOP_TWO == player.id) {
                    Service.getInstance().addEffectChar(player, 56, 1, -1, -1, -1);
//                    System.out.println(player.id);
                }
                switch (playerIdInt) {
                    case 16803:
                        Service.getInstance().addEffectChar(player, 80, 1, -1, -1, 1);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
