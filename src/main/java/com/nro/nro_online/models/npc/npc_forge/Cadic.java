package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.consts.ConstTranhNgocNamek;
import com.nro.nro_online.models.dragon_namec_war.TranhNgoc;
import com.nro.nro_online.models.dragon_namec_war.TranhNgocService;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.ServerManager;
import com.nro.nro_online.services.Service;

public class Cadic extends Npc {

    public Cadic(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        TranhNgoc tranhNgoc = ServerManager.gI().getTranhNgocManager().findByPLayerId(player.id);
        if (tranhNgoc != null && tranhNgoc.isFide(player)) {
            createOtherMenu(player, ConstNpc.BASE_MENU, "Cút!Ta không nói chuyện với sinh vật hạ đẳng", "Đóng");
            return;
        }
        createOtherMenu(player, ConstNpc.BASE_MENU, "Hãy mang ngọc rồng về cho ta", "Đưa ngọc", "Đóng");
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player))
            return;

        if (select == 0) {
            TranhNgoc tranhNgoc = ServerManager.gI().getTranhNgocManager().findByPLayerId(player.id);
            if (tranhNgoc == null || !tranhNgoc.isCadic(player) || !player.isHoldNamecBallTranhDoat)
                return;

            long waitTime = player.lastTimePickItem + 20000 - System.currentTimeMillis();
            if (waitTime > 0) {
                Service.getInstance().sendThongBao(player, "Vui lòng đợi " + (waitTime / 1000) + " giây để có thể trả");
                return;
            }

            TranhNgocService.getInstance().dropBall(player, (byte) 1);
            tranhNgoc.pointCadic = Math.min(tranhNgoc.pointCadic + 1, ConstTranhNgocNamek.MAX_POINT);
            TranhNgocService.getInstance().sendUpdatePoint(player);
        }
    }
}
