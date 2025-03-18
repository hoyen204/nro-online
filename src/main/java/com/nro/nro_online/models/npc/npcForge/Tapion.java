/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.npcForge;

import nro.consts.ConstNpc;
import nro.models.map.SantaCity;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.MapService;
import nro.services.Service;

/**
 *
 * @author Arriety
 */
public class Tapion extends Npc {

    public Tapion(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 19:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ác quỷ truyền thuyết Hirudegarn\nđã thoát khỏi phong ấn ngàn năm\nHãy giúp tôi chế ngự nó",
                            "OK", "Từ chối");
                    break;
                case 126:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Tôi sẽ đưa bạn về", "OK",
                            "Từ chối");
                    break;
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (this.mapId == 19) {
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0:
                            this.npcChat(player, "Chức năng tạm thời đóng ...");
//                                         SantaCity santaCity = (SantaCity) MapService.gI().getMapById(126);
//                            if (santaCity != null) {
//                                if (!santaCity.isOpened() || santaCity.isClosed()) {
//                                    Service.getInstance().sendThongBao(player,
//                                            "Hẹn gặp bạn lúc 22h mỗi ngày");
//                                    return;
//                                }
//                                santaCity.enter(player);
//                            } else {
//                                Service.getInstance().sendThongBao(player, "Có lỗi xảy ra!");
//                            }
                            break;
                    }
                }
            }
            if (this.mapId == 126) {
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0:
                            SantaCity santaCity = (SantaCity) MapService.gI().getMapById(126);
                            if (santaCity != null) {
                                santaCity.leave(player);
                            } else {
                                Service.getInstance().sendThongBao(player, "Có lỗi xảy ra!");
                            }
                            break;
                    }
                }
            }
        }
    }
}
