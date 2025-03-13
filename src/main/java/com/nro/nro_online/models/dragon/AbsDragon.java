package com.nro.nro_online.models.dragon;

import java.io.IOException;
import com.nro.nro_online.login.Cmd;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class AbsDragon implements Runnable {

    private static final long APPEARANCE_DURATION_MS = 60000 * 5;

    private String content;
    private String[] wishes;
    private String tutorial;
    private Player summoner;
    private long summonerID;
    private String name;
    private boolean appear;
    private long lastTimeAppear;

    public AbsDragon(Player player) {
        this.summoner = player;
        this.summonerID = player.id;
    }

    public abstract void openMenu();

    public abstract void summon();

    public abstract void reSummon();

    public abstract void showWishes();

    public void sendNotify() {
        Message m = new Message(Cmd.SERVER_MESSAGE);
        try {
            m.writer().writeUTF(summoner.name + " vừa gọi " + name + " tại "
                    + summoner.zone.map.mapName + " khu vực " + summoner.zone.zoneId);
            Service.getInstance().sendMessAllPlayerIgnoreMe(summoner, m);
        } catch (IOException e) {
            Log.error(AbsDragon.class, e, "Error sending notification");
        }
    }

    public abstract void callDragon();

    public abstract void leave();

    @Override
    public void run() {
        while (isAppear()) {
            if (Util.canDoWithTime(lastTimeAppear, APPEARANCE_DURATION_MS)) {
                leave();
            }
        }
    }
}
