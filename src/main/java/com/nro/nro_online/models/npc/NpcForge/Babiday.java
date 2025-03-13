/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.NpcForge;

import nro.consts.ConstNpc;
import nro.models.map.Zone;
import nro.models.map.mabu.MabuWar;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.MapService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.services.func.ChangeMapService;
import nro.utils.Util;

/**
 *
 * @author Arriety
 */
public class Babiday extends Npc {

    public Babiday(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (MapService.gI().isMapMabuWar(this.mapId) && MabuWar.gI().isTimeMabuWar()) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Đừng vội xem thường Babyđây,ngay đến cha hắn là thần ma đạo sĩ\n"
                        + "Bibiđây khi còn sống cũng phải sợ hắn đấy",
                        "Yểm bùa\n50Tr Vàng",
                        player.zone.map.mapId != 120 ? "Xuống\nTầng Dưới" : "Rời\nKhỏi đây");
            } else {
                super.openBaseMenu(player);
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (MapService.gI().isMapMabuWar(this.mapId) && MabuWar.gI().isTimeMabuWar()) {
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0:
                            if (player.inventory.getGold() >= 50000000) {
                                Service.getInstance().changeFlag(player, 10);
                                player.inventory.subGold(50000000);
                            } else {
                                Service.getInstance().sendThongBao(player, "Không đủ vàng");
                            }
                            break;
                        case 1:
                            if (player.zone.map.mapId == 120) {
                                ChangeMapService.gI().changeMapBySpaceShip(player,
                                        player.gender + 21, -1, 250);
                            }
                            if (player.cFlag == 10) {
                                if (player.getPowerPoint() >= 20) {
                                    if (!(player.zone.map.mapId == 119)) {
                                        int idMapNextFloor = player.zone.map.mapId == 115
                                                ? player.zone.map.mapId + 2
                                                : player.zone.map.mapId + 1;
                                        ChangeMapService.gI().changeMap(player, idMapNextFloor, -1,
                                                354, 240);
                                    } else {
                                        Zone zone = MabuWar.gI().getMapLastFloor(120);
                                        if (zone != null) {
                                            ChangeMapService.gI().changeMap(player, zone, 354, 240);
                                        } else {
                                            Service.getInstance().sendThongBao(player,
                                                    "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
                                            ChangeMapService.gI().changeMapBySpaceShip(player,
                                                    player.gender + 21, -1, 250);
                                        }
                                    }
                                    player.resetPowerPoint();
                                    player.sendMenuGotoNextFloorMabuWar = false;
                                    Service.getInstance().sendPowerInfo(player, "TL",
                                            player.getPowerPoint());
                                    if (Util.isTrue(1, 30)) {
                                        player.inventory.ruby += 1;
                                        PlayerService.gI().sendInfoHpMpMoney(player);
                                        Service.getInstance().sendThongBao(player,
                                                "Bạn nhận được 1 Hồng Ngọc");
                                    } else {
                                        Service.getInstance().sendThongBao(player,
                                                "Bạn đen vô cùng luôn nên không nhận được gì cả");
                                    }
                                } else {
                                    this.npcChat(player,
                                            "Ngươi cần có đủ điểm để xuống tầng tiếp theo");
                                }
                                break;
                            } else {
                                this.npcChat(player,
                                        "Ngươi đang theo phe Ôsin,Hãy qua bên đó mà thể hiện");
                            }
                    }
                }
            }
        }
    }

}
