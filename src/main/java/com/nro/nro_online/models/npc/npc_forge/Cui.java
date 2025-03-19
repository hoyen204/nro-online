package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.consts.ConstTask;
import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.boss.BossManager;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.NpcService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.TaskService;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Util;

public class Cui extends Npc {

    private static final int COST_FIND_BOSS = 20000000;

    public Cui(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player pl) {
        if (!canOpenNpc(pl)) return;
        
        if (TaskService.gI().checkDoneTaskTalkNpc(pl, this)) return;

        if (pl.playerTask.taskMain.id == 7) {
            NpcService.gI().createTutorial(pl, this.avartar, 
                "Hãy lên đường cứu đứa bé nhà tôi\nChắc bây giờ nó đang sợ hãi lắm rồi");
            return;
        }

        switch (this.mapId) {
            case 19:
                handleMapId19Menu(pl);
                break;
            case 68:
                this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                        "Ngươi muốn về Thành Phố Vegeta", "Đồng ý", "Từ chối");
                break;
            default:
                this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                        "Tàu vũ trụ Xayda sử dụng công nghệ mới nhất, " 
                        + "có thể đưa ngươi đi bất kỳ đâu, chỉ cần trả tiền là được.",
                        "Đến\nTrái Đất", "Đến\nNamếc", "Siêu thị");
                break;
        }
    }

    private void handleMapId19Menu(Player pl) {
        int taskId = TaskService.gI().getIdTask(pl);
        String baseMessage = "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó";
        String costBossText = "(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)";
        
        switch (taskId) {
            case ConstTask.TASK_19_0:
                this.createOtherMenu(pl, ConstNpc.MENU_FIND_KUKU, baseMessage,
                        "Đến chỗ\nKuku\n" + costBossText, "Đến Cold", "Đến\nNappa", "Từ chối");
                break;
            case ConstTask.TASK_19_1:
                this.createOtherMenu(pl, ConstNpc.MENU_FIND_MAP_DAU_DINH, baseMessage,
                        "Đến chỗ\nMập đầu đinh\n" + costBossText, "Đến Cold", "Đến\nNappa", "Từ chối");
                break;
            case ConstTask.TASK_19_2:
                this.createOtherMenu(pl, ConstNpc.MENU_FIND_RAMBO, baseMessage,
                        "Đến chỗ\nRambo\n" + costBossText, "Đến Cold", "Đến\nNappa", "Từ chối");
                break;
            default:
                this.createOtherMenu(pl, ConstNpc.BASE_MENU, baseMessage,
                        "Đến Cold", "Đến\nNappa", "Từ chối");
                break;
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) return;
        
        if (this.mapId == 26 && player.iDMark.isBaseMenu()) {
            handleMap26Teleport(player, select);
            return;
        }
        
        if (this.mapId == 19) {
            handleMap19Teleport(player, select);
            return;
        }
        
        if (this.mapId == 68 && player.iDMark.isBaseMenu() && select == 0) {
            ChangeMapService.gI().changeMapBySpaceShip(player, 19, -1, 1100);
        }
    }
    
    private void handleMap26Teleport(Player player, int select) {
        switch (select) {
            case 0:
                ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                break;
            case 1:
                ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                break;
            case 2:
                ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                break;
        }
    }
    
    private void handleMap19Teleport(Player player, int select) {
        if (player.iDMark.isBaseMenu()) {
            handleMap19BaseMenu(player, select);
            return;
        }
        
        int bossId = -1;
        switch (player.iDMark.getIndexMenu()) {
            case ConstNpc.MENU_FIND_KUKU:
                bossId = BossFactory.KUKU;
                break;
            case ConstNpc.MENU_FIND_MAP_DAU_DINH:
                bossId = BossFactory.MAP_DAU_DINH;
                break;
            case ConstNpc.MENU_FIND_RAMBO:
                bossId = BossFactory.RAMBO;
                break;
        }
        
        if (bossId != -1 && select == 0) {
            teleportToBoss(player, bossId);
        } else if (select == 1) {
            ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
        } else if (select == 2) {
            ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
        }
    }
    
    private void handleMap19BaseMenu(Player player, int select) {
        switch (select) {
            case 0:
                ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                break;
            case 1:
                ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                break;
        }
    }
    
    private void teleportToBoss(Player player, int bossId) {
        Boss boss = BossManager.gI().getBossById(bossId);
        if (boss != null && !boss.isDie()) {
            if (player.inventory.gold >= COST_FIND_BOSS) {
                player.inventory.gold -= COST_FIND_BOSS;
                ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
                Service.getInstance().sendMoney(player);
            } else {
                Service.getInstance().sendThongBao(player,
                        "Không đủ vàng, còn thiếu " + 
                        Util.numberToMoney(COST_FIND_BOSS - player.inventory.gold) + " vàng");
            }
        }
    }
}
