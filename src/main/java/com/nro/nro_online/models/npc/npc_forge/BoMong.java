package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.consts.ConstTask;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.TaskService;

public class BoMong extends Npc {
    
    private static final int[] VALID_MAP_IDS = {47, 84};
    
    public BoMong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }
    
    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player) || !isValidMap()) {
            return;
        }
        
        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Xin chào, cậu muốn tôi giúp gì?",
                "Nhiệm vụ\nhàng ngày", "Nhận ngọc\nmiễn phí", "Từ chối");
    }
    
    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player) || !isValidMap()) {
            return;
        }
        
        if (player.iDMark.isBaseMenu()) {
            handleBaseMenu(player, select);
        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK) {
            handleLevelSideTaskMenu(player, select);
        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PAY_SIDE_TASK) {
            handlePaySideTaskMenu(player, select);
        }
    }
    
    private boolean isValidMap() {
        for (int id : VALID_MAP_IDS) {
            if (this.mapId == id) {
                return true;
            }
        }
        return false;
    }
    
    private void handleBaseMenu(Player player, int select) {
        switch (select) {
            case 0:
                if (player.playerTask.sideTask.template != null) {
                    showSideTaskInfo(player);
                } else {
                    showSideTaskLevelOptions(player);
                }
                break;
            case 1:
                this.npcChat(player, "Bảo trì");
                break;
        }
    }
    
    private void showSideTaskInfo(Player player) {
        StringBuilder npcSay = new StringBuilder("Nhiệm vụ hiện tại: ")
                .append(player.playerTask.sideTask.getName())
                .append(" (")
                .append(player.playerTask.sideTask.getLevel())
                .append(")")
                .append("\nHiện tại đã hoàn thành: ")
                .append(player.playerTask.sideTask.count)
                .append("/")
                .append(player.playerTask.sideTask.maxCount)
                .append(" (")
                .append(player.playerTask.sideTask.getPercentProcess())
                .append("%)\nSố nhiệm vụ còn lại trong ngày: ")
                .append(player.playerTask.sideTask.leftTask)
                .append("/")
                .append(ConstTask.MAX_SIDE_TASK);
                
        this.createOtherMenu(player, ConstNpc.MENU_OPTION_PAY_SIDE_TASK,
                npcSay.toString(), "Trả nhiệm\nvụ", "Hủy nhiệm\nvụ");
    }
    
    private void showSideTaskLevelOptions(Player player) {
        this.createOtherMenu(player, ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK,
                "Tôi có vài nhiệm vụ theo cấp bậc, sức cậu có thể làm được cái nào?",
                "Dễ", "Bình thường", "Khó", "Siêu khó", "Từ chối");
    }
    
    private void handleLevelSideTaskMenu(Player player, int select) {
        if (select >= 0 && select <= 3) {
            TaskService.gI().changeSideTask(player, (byte) select);
        }
    }
    
    private void handlePaySideTaskMenu(Player player, int select) {
        switch (select) {
            case 0:
                TaskService.gI().paySideTask(player);
                break;
            case 1:
                TaskService.gI().removeSideTask(player);
                break;
        }
    }
}
