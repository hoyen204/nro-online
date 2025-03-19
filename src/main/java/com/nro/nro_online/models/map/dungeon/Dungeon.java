package com.nro.nro_online.models.map.dungeon;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import com.nro.nro_online.consts.Cmd;
import com.nro.nro_online.models.map.dungeon.zones.ZDungeon;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Log;

@Getter
@Setter
public abstract class Dungeon {

    public static final int SNAKE_ROAD = 2;

    protected final List<ZDungeon> zones = new ArrayList<>();
    protected int id;
    protected int type;
    protected int level;
    protected String name;
    protected long createdAt;
    protected int countDown;
    protected String title;
    protected boolean closed;
    protected boolean finish;

    public Dungeon(int level) {
        createdAt = System.currentTimeMillis();
        setLevel(level);
        init();
    }

    protected abstract void init();

    public void addZone(ZDungeon zone) {
        synchronized (zones) {
            zones.add(zone);
        }
    }

    public void removeZone(ZDungeon zone) {
        synchronized (zones) {
            zones.remove(zone);
        }
    }

    public ZDungeon find(int mapID) {
        synchronized (zones) {
            for (ZDungeon zone : zones) {
                if (zone.map.mapId == mapID) {
                    return zone;
                }
            }
        }
        return null;
    }

    public void update() {
        if (countDown > 0) {
            countDown--;
            if (countDown == 0) {
                close();
            }
        }
    }

    public abstract void finish();

    public abstract void join(Player player);

    protected void setTime(int countDown) {
        this.countDown = countDown;
        synchronized (zones) {
            zones.forEach((z) -> {
                z.setTextTime();
            });
        }
    }

    public void close() {
        if (!closed) {
            closed = true;
            synchronized (zones) {
                zones.forEach((z) -> {
                    z.close();
                });
                zones.clear();
            }
        }
    }

    public void sendNotification(String text) {
        try (Message ms = new Message(Cmd.SERVER_MESSAGE)) {
            DataOutputStream ds = ms.writer();
            ds.writeUTF(text);
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            Log.error(Dungeon.class, ex);
        }
    }

    public void sendMessage(Message ms) {
        synchronized (zones) {
            zones.forEach((zone) -> {
                Service.getInstance().sendMessAllPlayerInMap(zone, ms);
            });
        }
    }
}
