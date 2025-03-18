/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.npcForge;

import nro.consts.ConstNpc;
import nro.consts.ConstTask;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.NpcService;
import nro.services.Service;
import nro.services.TaskService;
import nro.services.func.ChangeMapService;

/**
 *
 * @author Arriety
 */
public class Calick extends Npc {

    public Calick(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        player.iDMark.setIndexMenu(ConstNpc.BASE_MENU);
        if (TaskService.gI().getIdTask(player) < ConstTask.TASK_20_0) {
            Service.getInstance().hideWaitDialog(player);
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (this.mapId == 102) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chào chú, cháu có thể giúp gì?",
                    "Kể\nChuyện", "Quay về\nQuá khứ");
        } else {
            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chào chú, cháu có thể giúp gì?",
                    "Kể\nChuyện", "Đi đến\nTương lai", "Từ chối");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (this.mapId == 102) {
            if (player.iDMark.isBaseMenu()) {
                if (select == 0) {
                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                } else if (select == 1) {
                    ChangeMapService.gI().goToQuaKhu(player);
                }
            }
        } else if (player.iDMark.isBaseMenu()) {
            switch (select) {
                case 0:
                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                    break;
                case 1:
                    if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_20_0) {
                        ChangeMapService.gI().goToTuongLai(player);
                    }
                    break;
                default:
                    Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                    break;
            }
        }
    }
}
