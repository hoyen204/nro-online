package com.nro.nro_online.services;

import java.util.List;

import com.nro.nro_online.consts.Cmd;
import com.nro.nro_online.manager.SieuHangManager;
import com.nro.nro_online.models.Part;
import com.nro.nro_online.models.PartManager;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.sieu_hang.SieuHangModel;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.utils.Util;

public class SieuHangService {

    public static void ShowTop(Player player, int can_fight) {
        List<SieuHangModel> list = SieuHangManager.GetTop((int) player.id, can_fight);
        try (Message msg = new Message(Cmd.TOP)) {
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100 Cao Thủ");
            msg.writer().writeByte(list.size());
            for (int i = 0; i < list.size(); i++) {
                int thuong = 0;
                SieuHangModel top = list.get(i);
                msg.writer().writeInt(top.rank);
                msg.writer().writeInt((int) top.player_id);
                msg.writer().writeShort(top.player.getHead());
                if (player.isVersionAbove(220)) {
                    Part part = PartManager.getInstance().find(top.player.getHead());
                    msg.writer().writeShort(part.getIcon(0));
                }
                msg.writer().writeShort(top.player.getBody());
                msg.writer().writeShort(top.player.getLeg());
                msg.writer().writeUTF(top.player.name);

                if (top.rank == 1) {
                    thuong = 20000;
                } else if (top.rank == 2) {
                    thuong = 15000;
                } else if (top.rank >= 3 && top.rank < 10) {
                    thuong = 10000;
                } else if (top.rank >= 10 && top.rank < 30) {
                    thuong = 7000;
                }  else {
                    thuong = 1;
                }
                if (top.rank <= 30) {
                    msg.writer().writeUTF("+" + thuong + " ngọc/ ngày");
                } else {
                    msg.writer().writeUTF("");
                }
                msg.writer().writeUTF("HP " + Util.formatCurrency(top.player.nPoint.hp) + "\n"
                        + "Sức đánh " + Util.formatCurrency(top.dame) + "\n"
                        + "Giáp " + Util.formatCurrency(top.defend) + "\n"
                        + top.message.replaceAll("/n", "\n"));
            }
            player.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
