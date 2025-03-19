package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.func.ChangeMapService;

public class Kibit extends Npc {

    public Kibit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player))
            return;

        if (this.mapId == 50) {
            createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?", "Đến\nKaio", "Từ chối");
        } else {
            super.openBaseMenu(player);
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player) || this.mapId != 50 || !player.iDMark.isBaseMenu() || select != 0)
            return;

        ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
    }
}