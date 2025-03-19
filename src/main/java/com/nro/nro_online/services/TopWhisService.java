package com.nro.nro_online.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.nro.nro_online.consts.Cmd;
import com.nro.nro_online.manager.TopWhis;
import com.nro.nro_online.models.Part;
import com.nro.nro_online.models.PartManager;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.whis.TopWhisModel;
import com.nro.nro_online.server.io.Message;

/**
 *
 * @author by Arriety
 */
public class TopWhisService {

    public static void ShowTop(Player player) {
        List<TopWhisModel> list = TopWhis.getTop();
        try (Message msg = new Message(Cmd.TOP)) {
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100");
            msg.writer().writeByte(list.size());
            for (int i = 0; i < list.size(); i++) {
                TopWhisModel top = list.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt((int) top.player.id);
                msg.writer().writeShort(top.player.getHead());
                if (player.isVersionAbove(220)) {
                    Part part = PartManager.getInstance().find(top.player.getHead());
                    msg.writer().writeShort(part.getIcon(0));
                }
                msg.writer().writeShort(top.player.getBody());
                msg.writer().writeShort(top.player.getLeg());
                msg.writer().writeUTF(top.player.name);
                msg.writer().writeUTF("LV:" + top.level + " với " + top.time_skill + " giây");

                Duration duration = Duration.between(top.last_time_attack, LocalDateTime.now());

                long hours = duration.toHours();
                long minutes = duration.toMinutesPart();
                long seconds = duration.toSecondsPart();

                msg.writer().writeUTF(hours + " giờ " + minutes + " phút " + seconds + " giây");
            }
            player.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
