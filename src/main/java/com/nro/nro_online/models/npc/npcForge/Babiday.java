package com.nro.nro_online.models.npc.npcForge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.map.mabu.MabuWar;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Util;

public class Babiday extends Npc {
    private static final long GOLD_COST = 50_000_000;
    private static final int POWER_POINT_REQUIRED = 20;
    private static final int HOME_MAP_OFFSET = 21;
    private static final int JOIN_X = 354;
    private static final int JOIN_Y = 240;

    public Babiday(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player))
            return;

        if (MapService.gI().isMapMabuWar(this.mapId) && MabuWar.gI().isTimeMabuWar()) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Đừng vội xem thường Babyđây, ngay đến cha hắn là thần ma đạo sĩ\n" +
                            "Bibiđây khi còn sống cũng phải sợ hắn đấy",
                    "Yểm bùa\n50Tr Vàng",
                    player.zone.map.mapId != 120 ? "Xuống\nTầng Dưới" : "Rời\nKhỏi đây");
        } else {
            super.openBaseMenu(player);
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player) || !MapService.gI().isMapMabuWar(this.mapId) || !MabuWar.gI().isTimeMabuWar() || !player.iDMark.isBaseMenu())
            return;

        Service service = Service.getInstance();
        if (select == 0) {
            if (player.inventory.getGold() >= GOLD_COST) {
                service.changeFlag(player, 10);
                player.inventory.subGold((int) GOLD_COST);
            } else {
                service.sendThongBao(player, "Không đủ vàng");
            }
        } else if (select == 1) {
            if (player.zone.map.mapId == 120) {
                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + HOME_MAP_OFFSET, -1, 250);
                return;
            }

            if (player.cFlag != 10) {
                this.npcChat(player, "Ngươi đang theo phe Ôsin, Hãy qua bên đó mà thể hiện");
                return;
            }

            if (player.getPowerPoint() < POWER_POINT_REQUIRED) {
                this.npcChat(player, "Ngươi cần có đủ điểm để xuống tầng tiếp theo");
                return;
            }

            handleNextFloor(player, service);
        }
    }

    private void handleNextFloor(Player player, Service service) {
        int currentMapId = player.zone.map.mapId;
        if (currentMapId != 119) {
            int nextMapId = currentMapId == 115 ? currentMapId + 2 : currentMapId + 1;
            ChangeMapService.gI().changeMap(player, nextMapId, -1, JOIN_X, JOIN_Y);
        } else {
            Zone zone = MabuWar.gI().getMapLastFloor(120);
            if (zone != null) {
                ChangeMapService.gI().changeMap(player, zone, JOIN_X, JOIN_Y);
            } else {
                service.sendThongBao(player, "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + HOME_MAP_OFFSET, -1, 250);
            }
        }

        player.resetPowerPoint();
        player.sendMenuGotoNextFloorMabuWar = false;
        service.sendPowerInfo(player, "TL", player.getPowerPoint());

        if (Util.isTrue(1, 30)) {
            player.inventory.ruby += 1;
            PlayerService.gI().sendInfoHpMpMoney(player);
            service.sendThongBao(player, "Bạn nhận được 1 Hồng Ngọc");
        } else {
            service.sendThongBao(player, "Bạn đen vô cùng luôn nên không nhận được gì cả");
        }
    }
}