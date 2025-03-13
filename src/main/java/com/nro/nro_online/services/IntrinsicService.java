package com.nro.nro_online.services;

import java.util.List;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.intrinsic.Intrinsic;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.utils.Util;

public class IntrinsicService {

    private static IntrinsicService i;
    private static final int[] COST_OPEN = {10, 20, 40, 80, 160, 320, 640, 1280};

    public static IntrinsicService gI() {
        if (i == null) {
            i = new IntrinsicService();
        }
        return i;
    }

    public List<Intrinsic> getIntrinsics(byte playerGender) {
        return switch (playerGender) {
            case 0 -> Manager.INTRINSIC_TD;
            case 1 -> Manager.INTRINSIC_NM;
            default -> Manager.INTRINSIC_XD;
        };
    }

    public Intrinsic getIntrinsicById(int id) {
        for (Intrinsic intrinsic : Manager.INTRINSICS) {
            if (intrinsic.id == id) {
                return new Intrinsic(intrinsic);
            }
        }
        return null;
    }

    public void sattd(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.menutd, -1,
                "Chọn đi ku", "Set\nKamejoko", "Set\nKaioken", "Set\nThên xin hăng", "Từ chối");
    }

    public void satnm(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.menunm, -1,
                "Chọn đi ku", "Set\nLiên hoàn", "Set\nPicolo", "Set\nPikkoro Daimao", "Từ chối");
    }

    public void setxd(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.menuxd, -1,
                "Chọn đi ku", "Set\nKakarot", "Set\nCađíc", "Set\nNappa", "Từ chối");
    }

    public void sendInfoIntrinsic(Player player) {
        Message msg;
        try {
            msg = new Message(112);
            msg.writer().writeByte(0);
            msg.writer().writeShort(player.playerIntrinsic.intrinsic.icon);
            msg.writer().writeUTF(player.playerIntrinsic.intrinsic.getName());
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void showAllIntrinsic(Player player) {
        List<Intrinsic> listIntrinsic = getIntrinsics(player.gender);
        Message msg;
        try {
            msg = new Message(112);
            msg.writer().writeByte(1);
            msg.writer().writeByte(1); //count tab
            msg.writer().writeUTF("Nội tại");
            msg.writer().writeByte(listIntrinsic.size() - 1);
            for (int i = 1; i < listIntrinsic.size(); i++) {
                msg.writer().writeShort(listIntrinsic.get(i).icon);
                msg.writer().writeUTF(listIntrinsic.get(i).getDescription());
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void showMenu(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.INTRINSIC, -1,
                "Nội tại là một kỹ năng bị động hỗ trợ đặc biệt\nBạn có muốn mở hoặc thay đổi nội tại không?",
                "Xem\ntất cả\nNội Tại", "Mở\nNội Tại", "Mở VIP", "Từ chối");
    }

    public void showConfirmOpen(Player player) {
        int index = player.playerIntrinsic.countOpen;
        if (index >= 0 && index < COST_OPEN.length) {
            NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_OPEN_INTRINSIC, -1, "Bạn muốn đổi Nội Tại khác\nvới giá là "
                    + COST_OPEN[index] + " Tr vàng ?", "Mở\nNội Tại", "Từ chối");
        } else {
            Service.getInstance().sendThongBao(player, "Lỗi");
        }
    }

    public void showConfirmOpenVip(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_OPEN_INTRINSIC_VIP, -1,
                "Bạn có muốn mở Nội Tại\nvới giá là 500 hồng ngọc và\ntái lập giá vàng quay lại ban đầu không?", "Mở\nNội VIP", "Từ chối");
    }

    private void changeIntrinsic(Player player) {
        List<Intrinsic> listIntrinsic = getIntrinsics(player.gender);
        player.playerIntrinsic.intrinsic = new Intrinsic(listIntrinsic.get(Util.nextInt(1, listIntrinsic.size() - 1)));
        player.playerIntrinsic.intrinsic.param1 = (short) Util.nextInt(player.playerIntrinsic.intrinsic.paramFrom1, player.playerIntrinsic.intrinsic.paramTo1);
        player.playerIntrinsic.intrinsic.param2 = (short) Util.nextInt(player.playerIntrinsic.intrinsic.paramFrom2, player.playerIntrinsic.intrinsic.paramTo2);
        Service.getInstance().sendThongBao(player, "Bạn nhận được Nội tại:\n" + player.playerIntrinsic.intrinsic.getName().substring(0, player.playerIntrinsic.intrinsic.getName().indexOf(" [")));
        sendInfoIntrinsic(player);
    }

    public void open(Player player) {
//        if (player.nPoint.power >= 10000000000L) {
//            int goldRequire = COST_OPEN[player.playerIntrinsic.countOpen] * 1000000;
//            if (player.inventory.gold >= goldRequire) {
//                player.inventory.gold -= goldRequire;
//                PlayerService.gI().sendInfoHpMpMoney(player);
//                changeIntrinsic(player);
//                player.playerIntrinsic.countOpen++;
//            } else {
//                Service.getInstance().sendThongBao(player, "Bạn không đủ vàng, còn thiếu "
//                        + Util.numberToMoney(goldRequire - player.inventory.gold) + " vàng nữa");
//            }
//        } else {
        Service.getInstance().sendThongBao(player, "Chỉ có thể sửa dụng Mở Nội Tại Víp");
//        }
    }

    public void openVip(Player player) {
        if (player.nPoint.power >= 10000000000L) {
            int ruby = 1_000;
            if (player.inventory.getRuby() >= 1_000) {
                player.inventory.subRuby(ruby);
                PlayerService.gI().sendInfoHpMpMoney(player);
                changeIntrinsic(player);
                player.playerIntrinsic.countOpen = 0;
            } else {
                Service.getInstance().sendThongBao(player, "Bạn không có đủ ngọc, còn thiếu "
                        + (ruby - player.inventory.getRuby()) + " hồng ngọc nữa");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Yêu cầu sức mạnh tối thiểu 10 tỷ");
        }
    }

}
