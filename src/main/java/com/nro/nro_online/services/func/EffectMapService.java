package com.nro.nro_online.services.func;

import com.nro.nro_online.consts.Cmd;
import com.nro.nro_online.models.map.EffectEventManager;
import com.nro.nro_online.models.map.EffectEventTemplate;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.Service;

public class EffectMapService {

    private static EffectMapService i;

    private EffectMapService() {

    }

    public static EffectMapService gI() {
        if (i == null) {
            i = new EffectMapService();
        }
        return i;
    }

    public void sendEffEvent(Player pl) {
        int plmapid = pl.zone.map.mapId;
        for (EffectEventTemplate i : EffectEventManager.gI().getList()) {
            if (Manager.EVENT_SEVER == i.getEventId()) {
                if (plmapid == i.getMapId()) {
                    EffectMapService.gI().sendEffectMapToPlayer(pl, i.getEffId(),
                            i.getLayer(), i.getLoop(), i.getX(), i.getY(), i.getDelay());
                }
            }
        }
    }

    public void sendEffectMapToPlayer(Player player, int id, int layer, int loop, int x, int y, int delay) {
        try (Message msg = new Message(113)) {
            msg.writer().writeByte(loop);
            msg.writer().writeByte(layer);
            msg.writer().writeByte(id);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            msg.writer().writeShort(delay);
            player.sendMessage(msg);
        } catch (Exception e) {
        }
    }

    public void sendCharEffect(Player player, byte type, short id, byte layer, byte loop, short delay, boolean isStand) {
        try (Message msg = new Message(Cmd.CHAR_EFFECT)) {
            msg.writer().writeByte(type);
            msg.writer().writeInt((int) player.id);
            if (type == 0) {
                msg.writer().writeShort(id);
                msg.writer().writeByte(layer);
                msg.writer().writeByte(loop);
                msg.writer().writeShort(delay);
                msg.writer().writeBoolean(isStand);
            } else if (type == 1) {
                msg.writer().writeShort(id);
            }
            player.sendMessage(msg);
        } catch (Exception e) {
        }
    }

    public void sendEffectMapToAllInMap(Zone zone, int id, int layer, int loop, int x, int y, int delay) {
        try (Message msg = new Message(113)) {
            msg.writer().writeByte(loop);
            msg.writer().writeByte(layer);
            msg.writer().writeByte(id);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            msg.writer().writeShort(delay);
            Service.getInstance().sendMessAllPlayerInMap(zone, msg);
        } catch (Exception e) {
        }
    }

}
