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
public class Goku extends Npc {

    public Goku(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 80:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Xin chào, tôi có thể giúp gì cho cậu?", "Tới hành tinh\nYardart",
                            "Từ chối");
                    break;
                case 131:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Xin chào, tôi có thể giúp gì cho cậu?", "Quay về", "Từ chối");
                    break;
                default:
                    super.openBaseMenu(player);
                    break;
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (player.iDMark.getIndexMenu()) {
                case ConstNpc.BASE_MENU:
                    if (this.mapId == 80) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, 131, -1, 940);
                        }
                    } else if (this.mapId == 131) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, 80, -1, 870);
                        }
                    }
                    break;
            }
        }
    }
}
