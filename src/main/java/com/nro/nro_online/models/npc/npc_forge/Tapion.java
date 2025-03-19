package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.map.SantaCity;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.Service;

public class Tapion extends Npc {

    public Tapion(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player))
            return;

        String message = this.mapId == 19
                ? "Ác quỷ truyền thuyết Hirudegarn\nđã thoát khỏi phong ấn ngàn năm\nHãy giúp tôi chế ngự nó"
                : "Tôi sẽ đưa bạn về";
        createOtherMenu(player, ConstNpc.BASE_MENU, message, "OK", "Từ chối");
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player) || !player.iDMark.isBaseMenu() || select != 0)
            return;

        SantaCity santaCity = (SantaCity) MapService.gI().getMapById(126);
        if (santaCity == null) {
            Service.getInstance().sendThongBao(player, "Có lỗi xảy ra!");
            return;
        }

        if (this.mapId == 19) {
            npcChat(player, "Chức năng tạm thời đóng ...");
        } else if (this.mapId == 126) {
            santaCity.leave(player);
        }
    }
}