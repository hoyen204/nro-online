/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.NpcForge;

import nro.consts.ConstNpc;
import nro.consts.ConstPlayer;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.TaskService;

/**
 *
 * @author Administrator
 */
public class Bunma extends Npc {

    public Bunma(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Cậu cần trang bị gì cứ đến chỗ tôi nhé", "Cửa\nhàng");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0:// Shop
                        if (player.gender == ConstPlayer.TRAI_DAT) {
                            this.openShopWithGender(player, ConstNpc.SHOP_BUNMA_QK_0, 0);
                        } else {
                            this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Xin lỗi cưng, chị chỉ bán đồ cho người Trái Đất", "Đóng");
                        }
                        break;
                }
            }
        }
    }
}
