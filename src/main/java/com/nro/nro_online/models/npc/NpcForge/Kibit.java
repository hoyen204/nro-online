/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.NpcForge;

import nro.consts.ConstNpc;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.func.ChangeMapService;

/**
 *
 * @author Arriety
 */
public class Kibit extends Npc {

    public Kibit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (this.mapId == 50) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                        "Đến\nKaio", "Từ chối");
            } else {
                super.openBaseMenu(player);
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (this.mapId == 50) {
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0:
                            ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                            break;
                    }
                }
            }
        }
    }

}
