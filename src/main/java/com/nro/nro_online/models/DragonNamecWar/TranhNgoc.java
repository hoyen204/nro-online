/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.DragonNamecWar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.nro.nro_online.consts.ConstTranhNgocNamek;
import com.nro.nro_online.models.map.Map;
import com.nro.nro_online.models.map.tranhngoc.TranhNgocZone;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.ServerManager;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstTranhNgocNamek;
import nro.models.map.Map;
import nro.models.map.tranhngoc.TranhNgocZone;
import nro.models.player.Player;
import nro.server.ServerManager;
import nro.services.MapService;
import nro.services.Service;
import nro.services.func.ChangeMapService;
import nro.utils.TimeUtil;
import nro.utils.Util;

/**
 *
 * @Build Arriety
 */
public class TranhNgoc {

    @Getter
    @Setter
    private int id;

    private boolean is_open;
    private boolean is_finish;
    private boolean closed;

    private static LocalDateTime TIME_OPEN;
    private static LocalDateTime TIME_CLOSE;
    private static LocalDateTime TIME_REGISTER;

    public static final byte HOUR_REGISTER = 19;
    public static final byte MIN_REGISTER = 0;
    public static final byte HOUR_OPEN = 19;
    public static final byte MIN_OPEN = 30;

    public static final byte HOUR_CLOSE = 20;
    public static final byte MIN_CLOSE = 0;

    @Getter
    private final List<Player> playersFide;
    @Getter
    private final List<Player> playersCadic;
    private TranhNgocZone zone;

    public int numOfPlayers;

    public int pointFide;
    public int pointCadic;

    public long lastTimeStartTranhNgoc;

    public TranhNgoc() {
        this.playersFide = new ArrayList<>();
        this.playersCadic = new ArrayList<>();
        ServerManager.gI().getTranhNgocManager().add(this);
        this.init();
        MapService.gI().getMapById(ConstTranhNgocNamek.MAP_ID);
        this.setTime();
    }

    private void init() {
        final ExecutorService threadPool = ServerManager.gI().getTranhNgocManager().getThreadPool();
        final Map map = MapService.gI().getMapById(ConstTranhNgocNamek.MAP_ID);
        final TranhNgocZone road = new TranhNgocZone(map, this.id, 10);
        this.zone = road;
    }

    public boolean isCadic(Player player) {
        for (Player pl : this.playersCadic) {
            if (pl.id == player.id) {
                return true;
            }
        }
        return false;
    }

    public boolean isFide(Player player) {
        for (Player pl : this.playersFide) {
            if (pl.id == player.id) {
                return true;
            }
        }
        return false;
    }

    public boolean addPlayersCadic(Player player) {
        boolean result = false;
        synchronized (playersCadic) {
            if (numOfPlayers < 10 && this.playersCadic.size() < 5 && !this.playersCadic.contains(player)) {
                this.playersCadic.add(player);
                numOfPlayers++;
                result = true;
            }
        }

        return result;
    }

    public boolean addPlayersFide(Player player) {
        boolean result = false;

        synchronized (playersFide) {
            if (numOfPlayers < 10 && this.playersFide.size() < 5 && !this.playersFide.contains(player)) {
                this.playersFide.add(player);
                numOfPlayers++;
                result = true;
            }
        }
        return result;
    }

    public void removePlayersCadic(Player player) {
        synchronized (playersCadic) {
            if (this.playersCadic.contains(player)) {
                this.playersCadic.remove(player);
                numOfPlayers--;
            }
        }
    }

    public void removePlayersFide(Player player) {
        synchronized (playersFide) {
            if (this.playersFide.contains(player)) {
                this.playersFide.remove(player);
                numOfPlayers--;
            }
        }
    }

    public void setTime() {
        try {
            String format = TimeUtil.getTimeNow("dd/MM/yyyy");
            TranhNgoc.TIME_OPEN = TimeUtil.getTime( format + " " + HOUR_OPEN + ":" + MIN_OPEN + ":" + 0, "dd/MM/yyyy HH:mm:ss");
            TranhNgoc.TIME_CLOSE = TimeUtil.getTime(format + " " + HOUR_CLOSE + ":" + MIN_CLOSE + ":" + 0, "dd/MM/yyyy HH:mm:ss");
            TranhNgoc.TIME_REGISTER = TimeUtil.getTime(format + " " + HOUR_REGISTER + ":" + MIN_REGISTER + ":" + 0, "dd/MM/yyyy HH:mm:ss");

            TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_REGISTER + ":" + MIN_REGISTER + ":" + 0, "dd/MM/yyyy HH:mm:ss");
        } catch (Exception e) {
        }
    }

    public void update() {
        try {
            if (!is_open) {
                if (isTimeStartWar()) {
                    is_open = true;
                    for (final Player player : this.getPlayersCadic()) {
                        if (player != null && player.zone.map.mapId != ConstTranhNgocNamek.MAP_ID) {
                            ChangeMapService.gI().changeMapInYard(player, this.zone, -1);
                            Service.getInstance().changeFlag(player, 1);
                            TranhNgocService.getInstance().sendCreatePhoBan(player);
                        }
                    }
                    for (final Player player : this.getPlayersFide()) {
                        if (player != null && player.zone.map.mapId != ConstTranhNgocNamek.MAP_ID) {
                            ChangeMapService.gI().changeMapInYard(player, this.zone, -1);
                            Service.getInstance().changeFlag(player, 2);
                            TranhNgocService.getInstance().sendCreatePhoBan(player);
                        }
                    }
                    lastTimeStartTranhNgoc = System.currentTimeMillis();
                    this.zone.is_open = true;
                }
            } else {
                updateZoneTranhNgoc();
            }
            if (this.is_finish) {
                this.close();
            }
            this.zone.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void JoinMap(Player player, int index) {
        ChangeMapService.gI().changeMapInYard(player, this.zone, -1);
        Service.getInstance().changeFlag(player, index);
    }

    public static boolean isTimeRegWar() {
        long now = System.currentTimeMillis();
        try {
            if (TranhNgoc.TIME_OPEN == 0 || TranhNgoc.TIME_REGISTER == 0) {
                TranhNgoc.TIME_OPEN = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_OPEN + ":" + MIN_OPEN + ":" + 0, "dd/MM/yyyy HH:mm:ss");
                TranhNgoc.TIME_CLOSE = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_CLOSE + ":" + MIN_CLOSE + ":" + 0, "dd/MM/yyyy HH:mm:ss");
                TranhNgoc.TIME_REGISTER = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_REGISTER + ":" + MIN_REGISTER + ":" + 0, "dd/MM/yyyy HH:mm:ss");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now > TIME_REGISTER && now < TIME_OPEN;
    }

    public static boolean isTimeStartWar() {
        long now = System.currentTimeMillis();
        try {
            if (TranhNgoc.TIME_OPEN == 0 || TranhNgoc.TIME_REGISTER == 0) {
                TranhNgoc.TIME_OPEN = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_OPEN + ":" + MIN_OPEN + ":" + 0, "dd/MM/yyyy HH:mm:ss");
                TranhNgoc.TIME_CLOSE = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_CLOSE + ":" + MIN_CLOSE + ":" + 0, "dd/MM/yyyy HH:mm:ss");
                TranhNgoc.TIME_REGISTER = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_REGISTER + ":" + MIN_REGISTER + ":" + 0, "dd/MM/yyyy HH:mm:ss");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now > TIME_OPEN && now < TIME_CLOSE;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.zone.close();
            playersCadic.clear();
            playersFide.clear();
        }
    }

    public void setClosed(final boolean closed) {
        this.closed = closed;
    }

    private void updateZoneTranhNgoc() {
        if (is_open) {
            if (Util.canDoWithTime(this.lastTimeStartTranhNgoc, ConstTranhNgocNamek.TIME)) {
                if (pointCadic > pointFide) {
                    SendWin(true);
                } else if (pointFide > pointCadic) {
                    SendWin(false);
                } else {
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.DRAW, true);
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.DRAW, false);
                }
            } else {
                if (pointCadic == 7) {
                    SendWin(true);
                } else if (pointFide == 7) {
                    SendWin(false);
                }
            }
        }
    }

    private void SendWin(boolean is_cadic) {
        if (is_cadic) {
            TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.WIN, false);
            TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.LOSE, true);
            TranhNgocService.getInstance().givePrice(getPlayersCadic(), ConstTranhNgocNamek.WIN, pointCadic);
            TranhNgocService.getInstance().givePrice(getPlayersFide(), ConstTranhNgocNamek.LOSE, pointFide);
        } else {
            TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.WIN, true);
            TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.LOSE, false);
            TranhNgocService.getInstance().givePrice(getPlayersFide(), ConstTranhNgocNamek.WIN, pointFide);
            TranhNgocService.getInstance().givePrice(getPlayersCadic(), ConstTranhNgocNamek.LOSE, pointCadic);
        }
        playersCadic.clear();
        playersFide.clear();
        pointCadic = 0;
        pointFide = 0;
        is_finish = true;
    }
}
