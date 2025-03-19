package com.nro.nro_online.models.npc.npc_forge;

import static com.nro.nro_online.models.npc.NpcFactory.PLAYERID_OBJECT;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.map.dungeon.SnakeRoad;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.ServerManager;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.services.func.Input;

public class ThanVuTru extends Npc {

    public ThanVuTru(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player) && mapId == 48) {
            createOtherMenu(player, ConstNpc.BASE_MENU, "Con muốn làm gì nào", "Di chuyển");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player) || mapId != 48)
            return;

        if (player.iDMark.isBaseMenu() && select == 0) {
            createOtherMenu(player, ConstNpc.MENU_DI_CHUYEN, "Con muốn đi đâu?",
                    "Về\nthần điện", "Thánh địa\nKaio", "Con\nđường\nrắn độc", "Từ chối");
        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DI_CHUYEN) {
            handleMapChange(player, select);
        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_CHON_CAP_DO && select == 0) {
            Input.gI().createFormChooseLevelCDRD(player);
        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_ACCEPT_GO_TO_CDRD && select == 0) {
            enterSnakeRoad(player);
        }
    }

    private void handleMapChange(Player player, int select) {
        switch (select) {
            case 0 -> ChangeMapService.gI().changeMapBySpaceShip(player, 45, -1, 354);
            case 1 -> ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
            case 2 -> handleSnakeRoadEntry(player);
        }
    }

    private void handleSnakeRoadEntry(Player player) {
        if (!player.getSession().actived) {
            Service.getInstance().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sử dụng chức năng này!");
            return;
        }
        if (player.clan == null) {
            Service.getInstance().sendThongBao(player, "Chỉ dành cho những người trong bang hội!");
            return;
        }
        if (player.clanMember.getNumDateFromJoinTimeToToday() < 2) {
            Service.getInstance().sendThongBao(player, "Phải tham gia bang hội ít nhất 2 ngày mới có thể tham gia!");
            return;
        }

        if (player.clan.snakeRoad == null) {
            createOtherMenu(player, ConstNpc.MENU_CHON_CAP_DO,
                    "Hãy mau trở về bằng con đường rắn độc\nbọn Xayda đã đến Trái Đất", "Chọn\ncấp độ", "Từ chối");
        } else if (player.clan.snakeRoad.isClosed()) {
            Service.getInstance().sendThongBao(player, "Bang hội đã hết lượt tham gia!");
        } else {
            createOtherMenu(player, ConstNpc.MENU_ACCEPT_GO_TO_CDRD,
                    "Con có chắc chắn muốn đến con đường rắn độc cấp độ " + player.clan.snakeRoad.getLevel() + "?",
                    "Đồng ý", "Từ chối");
        }
    }

    private void enterSnakeRoad(Player player) {
        if (player.clan == null || !player.getSession().actived)
            return;

        synchronized (player.clan) {
            if (player.clan.snakeRoad == null) {
                int level = Byte.parseByte(String.valueOf(PLAYERID_OBJECT.get(player.id)));
                SnakeRoad road = new SnakeRoad(level);
                ServerManager.gI().getDungeonManager().addDungeon(road);
                road.join(player);
                player.clan.snakeRoad = road;
            } else {
                player.clan.snakeRoad.join(player);
            }
        }
    }
}