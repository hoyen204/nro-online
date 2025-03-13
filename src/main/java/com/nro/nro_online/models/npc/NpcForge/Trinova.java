/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.NpcForge;

import nro.consts.ConstNpc;
import nro.consts.ConstTask;
import nro.jdbc.daos.PlayerDAO;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.PetService;
import nro.services.Service;
import nro.services.TaskService;
import nro.services.func.Input;

/**
 *
 * @author Arriety
 */
public class Trinova extends Npc {

    public Trinova(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    private static boolean nhanDeTu = true;

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "|8|SERVER NRO KIMKAN\n|2|Build Server: Arriety Béo\n|2|CEO, CCO, CMO, CHRO, CFO, CPO, KOL, DEV: Put đẹp trai"
                        + "\n|8|GIFTCODE: caitrang vatpham linhthu ngocrong saophale kimkan kichoat\n|8|phudeptrai hello tuanbeo bongcuoi bomong lixi2024 chucmung ",
                        // Server đang " + Client.gI().getPlayers().size() + " người Online"
                        "Nhận quà\nMiễn phí",
                        "Nhận Vàng\nVô hạn",
                        "Giftcode",
                        "Bỏ qua\nnhiệm vụ",
                        "Quy đổi\nHồng ngọc",
                        "Kích hoạt\nthành viên",
                        "Top Server");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0:
                        this.createOtherMenu(player, ConstNpc.QUA_TAN_THU,
                                "Ông có quà cho con đây này", "Nhận 100k\nNgọc xanh",
                                "Nhận\nĐệ tử");
                        break;
                    case 1:
                        if (player.inventory.gold >= 1000_000_000_000L) {
                            this.npcChat(player, "Sài hết rồi ta cho tiếp..");
                            break;
                        }
                        player.inventory.gold = 10_000_000_000L;
                        Service.getInstance().sendMoney(player);
                        Service.getInstance().sendThongBao(player, "Bạn vừa nhận được 10 tỏi vàng");
                        break;
                    case 2:
                        Input.gI().createFormGiftCode(player);
                        break;
                    case 3: {
                        switch (player.playerTask.taskMain.id) {
                            case 11:
                                switch (player.playerTask.taskMain.index) {
                                    case 0:
                                        TaskService.gI().DoneTask(player, ConstTask.TASK_11_0);
                                        break;
                                    case 1:
                                        TaskService.gI().DoneTask(player, ConstTask.TASK_11_1);
                                        break;
                                    case 2:
                                        TaskService.gI().DoneTask(player, ConstTask.TASK_11_2);
                                        break;
                                    default:
                                        Service.getInstance().sendThongBao(player, "Ta đã giúp con hoàn thành nhiệm vụ rồi mau đi trả nhiệm vụ");
                                        break;
                                }
                                break;

                            case 13:
                                if (player.playerTask.taskMain.index == 0) {
                                    TaskService.gI().DoneTask(player, ConstTask.TASK_13_0);
                                } else {
                                    Service.getInstance().sendThongBao(player, "Ta đã giúp con hoàn thành nhiệm vụ rồi mau đi trả nhiệm vụ");
                                }
                                break;
                            case 14:
                                switch (player.playerTask.taskMain.index) {
                                    case 0:
                                        for (int i = player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count; i < 30; i++) {
                                            TaskService.gI().DoneTask(player, ConstTask.TASK_14_0);
                                        }
                                        break;
                                    case 1:
                                        for (int i = player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count; i < 30; i++) {
                                            TaskService.gI().DoneTask(player, ConstTask.TASK_14_1);
                                        }
                                        break;
                                    case 2:
                                        for (int i = player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count; i < 30; i++) {
                                            TaskService.gI().DoneTask(player, ConstTask.TASK_14_2);
                                        }
                                        break;
                                    default:
                                        Service.getInstance().sendThongBao(player, "Ta đã giúp con hoàn thành nhiệm vụ rồi mau đi trả nhiệm vụ");
                                        break;
                                }
                                break;
                            case 15:
                                switch (player.playerTask.taskMain.index) {
                                    case 0:
                                        for (int i = player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count; i < 50; i++) {
                                            TaskService.gI().DoneTask(player, ConstTask.TASK_15_0);
                                        }
                                        break;
                                    case 1:
                                        for (int i = player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count; i < 50; i++) {
                                            TaskService.gI().DoneTask(player, ConstTask.TASK_15_1);
                                        }
                                        break;
                                    case 2:
                                        for (int i = player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count; i < 50; i++) {
                                            TaskService.gI().DoneTask(player, ConstTask.TASK_15_2);
                                        }
                                        break;
                                    default:
                                        Service.getInstance().sendThongBao(player, "Ta đã giúp con hoàn thành nhiệm vụ rồi mau đi trả nhiệm vụ");
                                        break;
                                }
                                break;
                            case 24:
                                switch (player.playerTask.taskMain.index) {
                                    case 0:
                                        TaskService.gI().DoneTask(player, ConstTask.TASK_24_0);
                                        break;
                                    case 1:
                                        TaskService.gI().DoneTask(player, ConstTask.TASK_24_1);
                                        break;
                                    case 2:
                                        TaskService.gI().DoneTask(player, ConstTask.TASK_24_2);
                                        break;
                                    case 3:
                                        TaskService.gI().DoneTask(player, ConstTask.TASK_24_3);
                                        break;
                                    case 4:
                                        for (int i = player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count; i < 690; i++) {
                                            TaskService.gI().DoneTask(player, ConstTask.TASK_24_4);
                                        }
                                        break;
                                    default:
                                        Service.getInstance().sendThongBao(player, "Ta đã giúp con hoàn thành nhiệm vụ rồi mau đi trả nhiệm vụ");
                                        break;
                                }
                                break;
                            default:
                                Service.getInstance().sendThongBao(player, "Nhiệm vụ hiện tại không thuộc diện hỗ trợ");
                                break;
                        }
                        break;
                    }
                    case 4:
                        if (!player.getSession().actived) {
                            this.npcChat(player, "Vui lòng kích hoạt tài khoản để sửa dụng nha coan");
                            return;
                        }
                        Input.gI().createFormTradeRuby(player);
                        break;
                    case 5:
                        if (player.getSession().actived) {
                            this.npcChat(player, "Con da kich hoat roi!");
                            return;
                        }
                        if (player.getSession().vnd >= 10_000) {
                            if (PlayerDAO.subVND2(player, 10_000)) {
                                Service.getInstance().sendThongBao(player, "Đã mở thành viên thành công!");
                            } else {
                                this.npcChat(player, "Lỗi vui lòng báo admin...");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Số dư vnd không đủ vui lòng nạp thêm tại:\nNROKIMKAN.ONLINE");
                        }
                        break;
                    case 6:
                        this.createOtherMenu(player, ConstNpc.MENU_SHOW_TOP,
                                "Ông sẽ cho con xem top của cả Server này!", "Top\nSức mạnh",
                                "Top\nNạp");
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_SHOW_TOP) {
                switch (select) {
                    case 0:
                        Service.ShowTopPower(player);
                        break;
                    case 1:
                        Service.ShowTopNap(player);
                        break;

                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.QUA_TAN_THU) {
                switch (select) {
                    case 0:
                        if (true) {
                            player.inventory.gem = 100000;
                            Service.getInstance().sendMoney(player);
                            Service.getInstance().sendThongBao(player,
                                    "Bạn vừa nhận được 100K ngọc xanh");
                            player.gift.gemTanThu = true;
                        } else {
                            this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Con đã nhận phần quà này rồi mà", "Đóng");
                        }
                        break;
                    case 1:
                        if (nhanDeTu) {
                            if (player.pet == null) {
                                PetService.gI().createNormalPet(player);
                                Service.getInstance().sendThongBao(player,
                                        "Bạn vừa nhận được đệ tử");
                            } else {
                                this.npcChat("Con đã nhận đệ tử rồi");
                            }
                        } else {
                            this.npcChat("Tính năng Nhận đệ tử đã đóng.");
                        }
                        break;
                    case 2:

                        break;

                }
            }
        }
    }

}
