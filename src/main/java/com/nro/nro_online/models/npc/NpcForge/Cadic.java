/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.NpcForge;

import nro.consts.ConstNpc;
import nro.consts.ConstTranhNgocNamek;
import nro.models.npc.Npc;
import nro.models.phuban.DragonNamecWar.TranhNgoc;
import nro.models.phuban.DragonNamecWar.TranhNgocService;
import nro.models.player.Player;
import nro.server.ServerManager;
import nro.services.Service;
import nro.utils.Util;

/**
 *
 * @author Arriety
 */
public class Cadic extends Npc {

    public Cadic(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        TranhNgoc tn = ServerManager.gI().getTranhNgocManager().findByPLayerId(player.id);
        if (tn.isFide(player)) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Cút!Ta không nói chuyện với sinh vật hạ đẳng", "Đóng");
            return;
        }
        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                "Hãy mang ngọc rồng về cho ta", "Đưa ngọc", "Đóng");
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (select) {
                case 0:
                    TranhNgoc tn = ServerManager.gI().getTranhNgocManager().findByPLayerId(player.id);

                    if (tn != null) {
                        if (tn.isCadic(player) && player.isHoldNamecBallTranhDoat) {
                            if (!Util.canDoWithTime(player.lastTimePickItem, 20000)) {
                                Service.getInstance().sendThongBao(player, "Vui lòng đợi " + ((player.lastTimePickItem + 20000 - System.currentTimeMillis()) / 1000) + " giây để có thể trả");
                                return;
                            }
                            TranhNgocService.getInstance().dropBall(player, (byte) 1);
                            tn.pointCadic++;
                            if (tn.pointCadic > ConstTranhNgocNamek.MAX_POINT) {
                                tn.pointCadic = ConstTranhNgocNamek.MAX_POINT;
                            }
                            TranhNgocService.getInstance().sendUpdatePoint(player);
                        }
                    }
                    break;
                case 1:
                    break;
            }
        }
    }
}
