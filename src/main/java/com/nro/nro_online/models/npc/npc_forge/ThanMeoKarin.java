package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstMap;
import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.map.dungeon.zones.ZSnakeRoad;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.TaskService;

public class ThanMeoKarin extends Npc {

    public ThanMeoKarin(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player) || mapId != ConstMap.THAP_KARIN)
            return;

        if (player.zone instanceof ZSnakeRoad) {
            createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Hãy cầm lấy hai hạt đậu cuối cùng ở đây\nCố giữ mình nhé " + player.name,
                    "Cảm ơn\nsư phụ");
        } else if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
            createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Con hãy bay theo cây Gậy Như Ý trên đỉnh tháp để đến Thần Điện gặp Thượng đế\nCon xứng đáng được làm đệ tử ông ấy!",
                    "Tập luyện\nvới\nThần mèo", "Từ chối");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player) || mapId != ConstMap.THAP_KARIN || !player.iDMark.isBaseMenu())
            return;

        if (player.zone instanceof ZSnakeRoad && select == 0) {
            player.setInteractWithKarin(true);
            Service.getInstance().sendThongBao(player, "Hãy mau bay xuống chân tháp Karin");
        } else if (select == 0) {
            npcChat(player, "Log");
        }
    }
}
