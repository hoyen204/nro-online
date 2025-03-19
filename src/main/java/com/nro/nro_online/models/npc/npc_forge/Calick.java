package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.consts.ConstTask;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.NpcService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.TaskService;
import com.nro.nro_online.services.func.ChangeMapService;

/**
 *
 * @author Arriety
 */
public class Calick extends Npc {

    private static final String DEFAULT_GREETING = "Chào chú, cháu có thể giúp gì?";
    private static final String REJECT_MESSAGE = "Không thể thực hiện";

    public Calick(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        player.iDMark.setIndexMenu(ConstNpc.BASE_MENU);
        
        if (TaskService.gI().getIdTask(player) < ConstTask.TASK_20_0) {
            Service.getInstance().hideWaitDialog(player);
            Service.getInstance().sendThongBao(player, REJECT_MESSAGE);
            return;
        }
        
        if (this.mapId == 102) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU, DEFAULT_GREETING,
                    "Kể\nChuyện", "Quay về\nQuá khứ");
        } else {
            this.createOtherMenu(player, ConstNpc.BASE_MENU, DEFAULT_GREETING,
                    "Kể\nChuyện", "Đi đến\nTương lai", "Từ chối");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!player.iDMark.isBaseMenu()) {
            return;
        }
        
        if (this.mapId == 102) {
            handleMap102Menu(player, select);
        } else {
            handleDefaultMapMenu(player, select);
        }
    }
    
    private void handleMap102Menu(Player player, int select) {
        switch (select) {
            case 0:
                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                break;
            case 1:
                ChangeMapService.gI().goToQuaKhu(player);
                break;
        }
    }
    
    private void handleDefaultMapMenu(Player player, int select) {
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
                Service.getInstance().sendThongBao(player, REJECT_MESSAGE);
                break;
        }
    }
}
