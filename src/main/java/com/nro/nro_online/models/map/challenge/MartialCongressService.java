package com.nro.nro_online.models.map.challenge;

import com.nro.nro_online.consts.ConstMap;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Log;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MartialCongressService {

    private static final MartialCongressService INSTANCE = new MartialCongressService();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static MartialCongressService gI() {
        return INSTANCE;
    }

    private MartialCongressService() {}

    public void startChallenge(Player player) {
        Zone zone = getMapChallenge(ConstMap.DAI_HOI_VO_THUAT_129);
        if (zone == null) {
            Service.getInstance().sendThongBao(player, "Không tìm thấy khu vực thi đấu!");
            return;
        }

        ChangeMapService.gI().changeMap(player, zone, player.location.x, 360);
        executor.schedule(() -> {
            MartialCongress mc = new MartialCongress();
            mc.setPlayer(player);
            mc.setNpc(zone.getReferee());
            mc.toTheNextRound();
            MartialCongressManager.gI().add(mc);
            Service.getInstance().sendThongBao(player, "Số thứ tự của ngươi là 1\nChuẩn bị thi đấu nhé!");
        }, 500, TimeUnit.MILLISECONDS);
    }

    public void moveFast(Player player, int x, int y) {
        sendMessage(player, 58, msg -> {
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            msg.writer().writeInt((int) player.id);
        });
    }

    public void sendTypePK(Player player, Player boss) {
        try{
            sendMessage(player, Service.getInstance().messageSubCommand((byte) 35), msg -> {
                msg.writer().writeInt((int) boss.id);
                msg.writer().writeByte(3);
            });
        }
        catch (IOException e){
            Log.error(this.getClass(), e);
        }
    }

    public Zone getMapChallenge(int mapId) {
        Zone zone = MapService.gI().getMapWithRandZone(mapId);
        return (zone != null && zone.getNumOfBosses() < 1) ? zone : null;
    }

    private void sendMessage(Player player, Object cmd, MessageConsumer consumer) {
        try (Message msg = (cmd instanceof Message) ? (Message) cmd : new Message((byte) cmd)) {
            consumer.accept(msg);
            player.sendMessage(msg);
        } catch (IOException e) {
            Log.error(this.getClass(), e);
        }
    }

    @FunctionalInterface
    private interface MessageConsumer {
        void accept(Message msg) throws IOException;
    }
}