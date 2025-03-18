package com.nro.nro_online.models.npc.npcForge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.consts.ConstTask;
import com.nro.nro_online.jdbc.daos.PlayerDAO;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.PetService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.TaskService;
import com.nro.nro_online.services.func.Input;

public class Trinova extends Npc {
    private static final long GOLD_REWARD = 10_000_000_000L;
    private static final int GEM_REWARD = 100_000;
    private static final int VND_ACTIVATION_COST = 10_000;
    private static boolean nhanDeTu = true;

    public Trinova(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player) || TaskService.gI().checkDoneTaskTalkNpc(player, this))
            return;

        createOtherMenu(player, ConstNpc.BASE_MENU,
                "|8|SERVER NRO KIMKAN\n|2|Build Server: Arriety Béo\n|2|CEO, CCO, CMO, CHRO, CFO, CPO, KOL, DEV: Put đẹp trai"
                        + "\n|8|GIFTCODE: caitrang vatpham linhthu ngocrong saophale kimkan kichoat"
                        + "\n|8|phudeptrai hello tuanbeo bongcuoi bomong lixi2024 chucmung",
                "Nhận quà\nMiễn phí", "Nhận Vàng\nVô hạn", "Giftcode", "Bỏ qua\nnhiệm vụ",
                "Quy đổi\nHồng ngọc", "Kích hoạt\nthành viên", "Top Server");
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player))
            return;

        if (player.iDMark.isBaseMenu()) {
            handleBaseMenu(player, select);
        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_SHOW_TOP) {
            handleTopMenu(player, select);
        } else if (player.iDMark.getIndexMenu() == ConstNpc.QUA_TAN_THU) {
            handleNewbieGiftMenu(player, select);
        }
    }

    private void handleBaseMenu(Player player, int select) {
        Service service = Service.getInstance();
        switch (select) {
        case 0 -> createOtherMenu(player, ConstNpc.QUA_TAN_THU,
                "Ông có quà cho con đây này", "Nhận 100k\nNgọc xanh", "Nhận\nĐệ tử");
        case 1 -> {
            if (player.inventory.gold >= 1_000_000_000_000L) {
                npcChat(player, "Sài hết rồi ta cho tiếp..");
            } else {
                player.inventory.gold = GOLD_REWARD;
                service.sendMoney(player);
                service.sendThongBao(player, "Bạn vừa nhận được 10 tỏi vàng");
            }
        }
        case 2 -> Input.gI().createFormGiftCode(player);
        case 3 -> skipTask(player);
        case 4 -> {
            if (!player.getSession().actived) {
                npcChat(player, "Vui lòng kích hoạt tài khoản để sử dụng nha con");
            } else {
                Input.gI().createFormTradeRuby(player);
            }
        }
        case 5 -> activateAccount(player);
        case 6 -> createOtherMenu(player, ConstNpc.MENU_SHOW_TOP,
                "Ông sẽ cho con xem top của cả Server này!", "Top\nSức mạnh", "Top\nNạp");
        }
    }

    private void handleTopMenu(Player player, int select) {
        switch (select) {
        case 0 -> Service.ShowTopPower(player);
        case 1 -> Service.ShowTopNap(player);
        }
    }

    private void handleNewbieGiftMenu(Player player, int select) {
        Service service = Service.getInstance();
        switch (select) {
        case 0 -> {
            if (!player.gift.gemTanThu) {
                player.inventory.gem = GEM_REWARD;
                service.sendMoney(player);
                service.sendThongBao(player, "Bạn vừa nhận được 100K ngọc xanh");
                player.gift.gemTanThu = true;
            } else {
                createOtherMenu(player, ConstNpc.IGNORE_MENU,
                        "Con đã nhận phần quà này rồi mà", "Đóng");
            }
        }
        case 1 -> {
            if (!nhanDeTu) {
                npcChat(player, "Tính năng Nhận đệ tử đã đóng.");
            } else if (player.pet == null) {
                PetService.gI().createNormalPet(player);
                service.sendThongBao(player, "Bạn vừa nhận được đệ tử");
            } else {
                npcChat(player, "Con đã nhận đệ tử rồi");
            }
        }
        }
    }

    private void skipTask(Player player) {
        Service service = Service.getInstance();
        int taskId = player.playerTask.taskMain.id;
        int index = player.playerTask.taskMain.index;

        switch (taskId) {
        case 11:
            skipSimpleTask(player, index, ConstTask.TASK_11_0, ConstTask.TASK_11_1, ConstTask.TASK_11_2);
            break;
        case 13:
            if (index == 0)
                TaskService.gI().DoneTask(player, ConstTask.TASK_13_0);
            else
                service.sendThongBao(player, "Ta đã giúp con hoàn thành nhiệm vụ rồi, mau đi trả nhiệm vụ");
            break;
        case 14:
            skipCountTask(player, index, 30, ConstTask.TASK_14_0, ConstTask.TASK_14_1, ConstTask.TASK_14_2);
            break;
        case 15:
            skipCountTask(player, index, 50, ConstTask.TASK_15_0, ConstTask.TASK_15_1, ConstTask.TASK_15_2);
            break;
        case 24:
            skipTask24(player, index);
            break;
        default:
            service.sendThongBao(player, "Nhiệm vụ hiện tại không thuộc diện hỗ trợ");
            break;
        }
    }

    private void skipSimpleTask(Player player, int index, int task0, int task1, int task2) {
        Service service = Service.getInstance();
        switch (index) {
        case 0:
            TaskService.gI().DoneTask(player, task0);
            break;
        case 1:
            TaskService.gI().DoneTask(player, task1);
            break;
        case 2:
            TaskService.gI().DoneTask(player, task2);
            break;
        default:
            service.sendThongBao(player, "Ta đã giúp con hoàn thành nhiệm vụ rồi, mau đi trả nhiệm vụ");
            break;
        }
    }

    private void skipCountTask(Player player, int index, int targetCount, int task0, int task1, int task2) {
        Service service = Service.getInstance();
        TaskService taskService = TaskService.gI();
        switch (index) {
        case 0:
            completeTaskCount(player, taskService, targetCount, task0);
            break;
        case 1:
            completeTaskCount(player, taskService, targetCount, task1);
            break;
        case 2:
            completeTaskCount(player, taskService, targetCount, task2);
            break;
        default:
            service.sendThongBao(player, "Ta đã giúp con hoàn thành nhiệm vụ rồi, mau đi trả nhiệm vụ");
            break;
        }
    }

    private void completeTaskCount(Player player, TaskService taskService, int targetCount, int taskId) {
        int currentCount = player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count;
        for (int i = currentCount; i < targetCount; i++) {
            taskService.DoneTask(player, taskId);
        }
    }

    private void skipTask24(Player player, int index) {
        Service service = Service.getInstance();
        TaskService taskService = TaskService.gI();
        switch (index) {
        case 0:
            taskService.DoneTask(player, ConstTask.TASK_24_0);
            break;
        case 1:
            taskService.DoneTask(player, ConstTask.TASK_24_1);
            break;
        case 2:
            taskService.DoneTask(player, ConstTask.TASK_24_2);
            break;
        case 3:
            taskService.DoneTask(player, ConstTask.TASK_24_3);
            break;
        case 4:
            completeTaskCount(player, taskService, 690, ConstTask.TASK_24_4);
            break;
        default:
            service.sendThongBao(player, "Ta đã giúp con hoàn thành nhiệm vụ rồi, mau đi trả nhiệm vụ");
            break;
        }
    }

    private void activateAccount(Player player) {
        Service service = Service.getInstance();
        if (player.getSession().actived) {
            npcChat(player, "Con đã kích hoạt rồi!");
        } else if (player.getSession().vnd >= VND_ACTIVATION_COST) {
            if (PlayerDAO.subVND2(player, VND_ACTIVATION_COST)) {
                service.sendThongBao(player, "Đã mở thành viên thành công!");
            } else {
                npcChat(player, "Lỗi vui lòng báo admin...");
            }
        } else {
            service.sendThongBao(player, "Số dư VND không đủ, vui lòng nạp thêm tại:\nNROKIMKAN.ONLINE");
        }
    }
}