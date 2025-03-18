package com.nro.nro_online.models.npc.npcForge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.map.mabu.MabuWar;
import com.nro.nro_online.models.map.mabu.MabuWar14h;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.TaskService;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Util;

public class Osin extends Npc {
    private static final long GOLD_COST = 50_000_000;
    private static final int RUBY_COST = 55;
    private static final int POWER_POINT_REQUIRED = 20;
    private static final int HOME_MAP_OFFSET = 21;
    private static final int JOIN_X = 354;
    private static final int JOIN_Y = 240;

    public Osin(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player)) return;

        if (this.mapId == 50) {
            createMenu(player, "Ta có thể giúp gì cho ngươi ?", "Đến\nKaio", "Đến\nhành tinh\nBill", "Từ chối");
        } else if (this.mapId == 52) {
            handleMap52Menu(player);
        } else if (this.mapId == 154) {
            createMenu(player, "Ta có thể giúp gì cho ngươi ?", "Về thánh địa", "Đến\nhành tinh\nngục tù", "Từ chối");
        } else if (this.mapId == 155) {
            createMenu(player, "Ta có thể giúp gì cho ngươi ?", "Quay về", "Từ chối");
        } else if (MapService.gI().isMapMabuWar(this.mapId)) {
            handleMabuWarMenu(player);
        } else {
            super.openBaseMenu(player);
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player) || !player.iDMark.isBaseMenu()) return;

        Service service = Service.getInstance();
        switch (this.mapId) {
        case 50:
            handleMap50(player, select);
            break;
        case 52:
            if (select == 0) ChangeMapService.gI().changeMap(player, 114, Util.nextInt(0, 24), JOIN_X, JOIN_Y);
            break;
        case 154:
            handleMap154(player, select, service);
            break;
        case 155:
            if (select == 0) ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
            break;
        default:
            if (MapService.gI().isMapMabuWar(this.mapId)) handleMabuWar(player, select, service);
            break;
        }
    }

    private void createMenu(Player player, String text, String... options) {
        this.createOtherMenu(player, ConstNpc.BASE_MENU, text, options);
    }

    private void handleMap52Menu(Player player) {
        String text = (MabuWar.gI().isTimeMabuWar() || MabuWar14h.gI().isTimeMabuWar())
                ? "Bây giờ tôi sẽ bí mật...\n đuổi theo 2 tên đồ tể...\nQuý vị nào muốn đi theo thì xin mời !"
                : "Vào lúc 12h tôi sẽ bí mật...\n đuổi theo 2 tên đồ tể...\nQuý vị nào muốn đi theo thì xin mời !";
        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
            this.createOtherMenu(player, ConstNpc.IGNORE_MENU, text, "Ok", "Từ chối");
        }
    }

    private void handleMabuWarMenu(Player player) {
        if (MabuWar.gI().isTimeMabuWar()) {
            createMenu(player,
                    "Đừng vội xem thường Babyđây, ngay đến cha hắn là thần ma đạo sĩ\nBibiđây khi còn sống cũng phải sợ hắn đấy",
                    "Giải trừ\nphép thuật\n50Tr Vàng",
                    player.zone.map.mapId != 120 ? "Xuống\nTầng Dưới" : "Rời\nKhỏi đây");
        } else if (MabuWar14h.gI().isTimeMabuWar()) {
            createMenu(player,
                    "Ta sẽ phù hộ cho ngươi bằng nguồn sức mạnh của Thần Kaiô\n+1 triệu HP, +1 triệu MP, +10k Sức đánh\nLưu ý: sức mạnh sẽ biến mất khi ngươi rời khỏi đây",
                    "Phù hộ\n55 hồng ngọc", "Từ chối", "Về\nĐại Hội\nVõ Thuật");
        }
    }

    private void handleMap50(Player player, int select) {
        if (select == 0) ChangeMapService.gI().changeMap(player, 48, -1, JOIN_X, JOIN_Y);
        else if (select == 1) ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
    }

    private void handleMap154(Player player, int select, Service service) {
        if (select == 0) {
            ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
        } else if (select == 1) {
            if (!Manager.gI().getGameConfig().isOpenPrisonPlanet()) {
                service.sendThongBao(player, "Lối vào hành tinh ngục tù chưa mở");
            } else if (player.nPoint.power < 60_000_000_000L) {
                service.sendThongBao(player, "Yêu cầu tối thiếu 60tỷ sức mạnh");
            } else if (player.playerTask.taskMain.id < 22) {
                this.npcChat(player, "Bạn phải hoàn thành xong nhiệm vụ Fide mới được qua!!!");
            } else {
                ChangeMapService.gI().changeMap(player, 155, -1, 111, 792);
            }
        }
    }

    private void handleMabuWar(Player player, int select, Service service) {
        if (MabuWar.gI().isTimeMabuWar()) {
            handleMabuWar12h(player, select, service);
        } else if (MabuWar14h.gI().isTimeMabuWar()) {
            handleMabuWar14h(player, select, service);
        }
    }

    private void handleMabuWar12h(Player player, int select, Service service) {
        if (select == 0) {
            if (player.inventory.getGold() >= GOLD_COST) {
                service.changeFlag(player, 9);
                player.inventory.subGold(GOLD_COST);
            } else {
                service.sendThongBao(player, "Không đủ vàng");
            }
        } else if (select == 1) {
            if (player.zone.map.mapId == 120) {
                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + HOME_MAP_OFFSET, -1, 250);
            } else if (player.cFlag == 9) {
                handleNextFloor(player, service);
            } else {
                this.npcChat(player, "Ngươi đang theo phe Babiđây, Hãy qua bên đó mà thể hiện");
            }
        }
    }

    private void handleMabuWar14h(Player player, int select, Service service) {
        if (select == 0) {
            if (player.effectSkin.isPhuHo) {
                this.npcChat("Con đã mang trong mình sức mạnh của thần Kaiô!");
            } else if (player.inventory.ruby < RUBY_COST) {
                service.sendThongBao(player, "Bạn không đủ hồng ngọc");
            } else {
                player.inventory.ruby -= RUBY_COST;
                player.effectSkin.isPhuHo = true;
                service.point(player);
                this.npcChat("Ta đã phù hộ cho con hãy giúp ta tiêu diệt Mabư!");
            }
        } else if (select == 2) {
            ChangeMapService.gI().changeMapBySpaceShip(player, 52, -1, 250);
        }
    }

    private void handleNextFloor(Player player, Service service) {
        if (player.getPowerPoint() < POWER_POINT_REQUIRED) {
            this.npcChat(player, "Ngươi cần có đủ điểm để xuống tầng tiếp theo");
            return;
        }

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
            }
        }

        player.resetPowerPoint();
        player.sendMenuGotoNextFloorMabuWar = false;
        service.sendPowerInfo(player, "%", player.getPowerPoint());
        awardRuby(player, service);
    }

    private void awardRuby(Player player, Service service) {
        if (Util.isTrue(1, 30)) {
            player.inventory.ruby += 1;
            PlayerService.gI().sendInfoHpMpMoney(player);
            service.sendThongBao(player, "Bạn nhận được 1 Hồng Ngọc");
        } else {
            service.sendThongBao(player, "Bạn đen vô cùng luôn nên không nhận được gì cả");
        }
    }
}