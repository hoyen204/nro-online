package com.nro.nro_online.models.dragon_namec_war;

import com.nro.nro_online.consts.ConstTranhNgocNamek;
import com.nro.nro_online.models.map.Map;
import com.nro.nro_online.models.map.tranhngoc.TranhNgocZone;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.ServerManager;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.TimeUtil;
import com.nro.nro_online.utils.Util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TranhNgoc {
    private int id;
    private boolean isOpen;
    private boolean isFinish;
    private boolean closed;

    private static LocalDateTime TIME_OPEN;
    private static LocalDateTime TIME_CLOSE;
    private static LocalDateTime TIME_REGISTER;

    public static final byte HOUR_REGISTER = 19; // 7h tối, giờ đăng ký nè! 😜
    public static final byte MIN_REGISTER = 0;
    public static final byte HOUR_OPEN = 19;    // 7h30 tối, bắt đầu chiến!
    public static final byte MIN_OPEN = 30;
    public static final byte HOUR_CLOSE = 20;   // 8h tối, nghỉ khỏe nha!
    public static final byte MIN_CLOSE = 0;

    private final List<Player> playersFide = new ArrayList<>();
    private final List<Player> playersCadic = new ArrayList<>();
    private TranhNgocZone zone;

    public int numOfPlayers;
    public int pointFide;
    public int pointCadic;
    private long lastTimeStartTranhNgoc;

    private static final int MAX_PLAYERS_PER_TEAM = 5; // 5 người mỗi đội, đủ là full! 😎
    private static final int MAX_TOTAL_PLAYERS = 10;   // Tổng 10 người, đông vui lắm rồi!

    public TranhNgoc() {
        ServerManager.gI().getTranhNgocManager().add(this);
        initZone();
        setTime();
    }

    private void initZone() {
        Map map = MapService.gI().getMapById(ConstTranhNgocNamek.MAP_ID);
        this.zone = new TranhNgocZone(map, this.id, MAX_TOTAL_PLAYERS); // Khởi tạo zone ngay, nhanh gọn lẹ! 🚀
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public List<Player> getPlayersFide() { return playersFide; }
    public List<Player> getPlayersCadic() { return playersCadic; }

    public boolean isCadic(Player player) {
        return playersCadic.stream().anyMatch(pl -> pl.id == player.id);
    }

    public boolean isFide(Player player) {
        return playersFide.stream().anyMatch(pl -> pl.id == player.id);
    }

    public boolean addPlayersCadic(Player player) {
        synchronized (playersCadic) {
            if (numOfPlayers >= MAX_TOTAL_PLAYERS || playersCadic.size() >= MAX_PLAYERS_PER_TEAM || playersCadic.contains(player)) {
                return false; // Full hoặc đã có, nghỉ nha! 😅
            }
            playersCadic.add(player);
            numOfPlayers++;
            return true;
        }
    }

    public boolean addPlayersFide(Player player) {
        synchronized (playersFide) {
            if (numOfPlayers >= MAX_TOTAL_PLAYERS || playersFide.size() >= MAX_PLAYERS_PER_TEAM || playersFide.contains(player)) {
                return false; // Full hoặc đã có, bye bye! 👋
            }
            playersFide.add(player);
            numOfPlayers++;
            return true;
        }
    }

    public void removePlayersCadic(Player player) {
        synchronized (playersCadic) {
            if (playersCadic.remove(player)) numOfPlayers--;
        }
    }

    public void removePlayersFide(Player player) {
        synchronized (playersFide) {
            if (playersFide.remove(player)) numOfPlayers--;
        }
    }

    private void setTime() {
        try {
            String today = TimeUtil.getTimeNow("dd/MM/yyyy");
            TIME_REGISTER = TimeUtil.getTime(today + " " + HOUR_REGISTER + ":" + MIN_REGISTER + ":0", "dd/MM/yyyy HH:mm:ss");
            TIME_OPEN = TimeUtil.getTime(today + " " + HOUR_OPEN + ":" + MIN_OPEN + ":0", "dd/MM/yyyy HH:mm:ss");
            TIME_CLOSE = TimeUtil.getTime(today + " " + HOUR_CLOSE + ":" + MIN_CLOSE + ":0", "dd/MM/yyyy HH:mm:ss");
        } catch (Exception e) {
            e.printStackTrace(); // Lỗi set thời gian thì kệ, in ra cho vui 😂
        }
    }

    public void update() {
        if (closed) return; // Đã đóng thì nghỉ, khỏi làm gì! 😛

        try {
            if (!isOpen && isTimeStartWar()) {
                startWar(); // Giờ chiến đấu, vào map thôi bro!
            } else if (isOpen) {
                updateZoneTranhNgoc(); // Cập nhật trận đấu, căng lắm nha! ⚡
            }
            if (isFinish) close(); // Kết thúc thì dọn dẹp, chill đi!
            zone.update();
        } catch (Exception e) {
            e.printStackTrace(); // Lỗi thì in ra, ai quan tâm đâu mà 😂
        }
    }

    private void startWar() {
        isOpen = true;
        playersCadic.forEach(pl -> moveToWarZone(pl, 1));
        playersFide.forEach(pl -> moveToWarZone(pl, 2));
        lastTimeStartTranhNgoc = System.currentTimeMillis();
        zone.setOpen(true); // Mở zone, chiến thôi nào! 🚀
    }

    private void moveToWarZone(Player player, int flag) {
        if (player != null && player.zone.map.mapId != ConstTranhNgocNamek.MAP_ID) {
            ChangeMapService.gI().changeMapInYard(player, zone, -1);
            Service.getInstance().changeFlag(player, flag);
            TranhNgocService.getInstance().sendCreatePhoBan(player);
        }
    }

    public void joinMap(Player player, int flag) {
        ChangeMapService.gI().changeMapInYard(player, zone, -1);
        Service.getInstance().changeFlag(player, flag);
    }

    public static boolean isTimeRegWar() {
        long now = System.currentTimeMillis();
        checkTimeReset();
        return now > TIME_REGISTER.toEpochSecond(java.time.ZoneOffset.UTC) * 1000 && now < TIME_OPEN.toEpochSecond(java.time.ZoneOffset.UTC) * 1000;
    }

    public static boolean isTimeStartWar() {
        long now = System.currentTimeMillis();
        checkTimeReset();
        return now > TIME_OPEN.toEpochSecond(java.time.ZoneOffset.UTC) * 1000 && now < TIME_CLOSE.toEpochSecond(java.time.ZoneOffset.UTC) * 1000;
    }

    private static void checkTimeReset() {
        if (TIME_OPEN == null || TIME_REGISTER == null || TIME_CLOSE == null) {
            new TranhNgoc().setTime(); // Reset thời gian nếu null, an toàn là trên hết! 😎
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() {
        if (!closed) {
            closed = true;
            zone.close();
            playersCadic.clear();
            playersFide.clear(); // Dọn sạch, về nhà thôi bro! 👋
        }
    }

    private void updateZoneTranhNgoc() {
        if (!Util.canDoWithTime(lastTimeStartTranhNgoc, ConstTranhNgocNamek.TIME)) {
            endWarByTime();
        } else if (pointCadic == 7) {
            endWar(true); // Cadíc thắng, quá đỉnh! 🎉
        } else if (pointFide == 7) {
            endWar(false); // Fide thắng, cũng ngon lắm! 🎉
        }
    }

    private void endWarByTime() {
        if (pointCadic > pointFide) {
            endWar(true);
        } else if (pointFide > pointCadic) {
            endWar(false);
        } else {
            TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.DRAW, true);
            TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.DRAW, false);
            isFinish = true; // Hòa, ai cũng buồn 😂
        }
    }

    private void endWar(boolean isCadicWin) {
        if (isCadicWin) {
            TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.WIN, false);
            TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.LOSE, true);
            TranhNgocService.getInstance().givePrice(playersCadic, ConstTranhNgocNamek.WIN, pointCadic);
            TranhNgocService.getInstance().givePrice(playersFide, ConstTranhNgocNamek.LOSE, pointFide);
        } else {
            TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.WIN, true);
            TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.LOSE, false);
            TranhNgocService.getInstance().givePrice(playersFide, ConstTranhNgocNamek.WIN, pointFide);
            TranhNgocService.getInstance().givePrice(playersCadic, ConstTranhNgocNamek.LOSE, pointCadic);
        }
        resetWar(); // Dọn dẹp sau trận, xong việc rồi! 😜
    }

    private void resetWar() {
        playersCadic.clear();
        playersFide.clear();
        pointCadic = 0;
        pointFide = 0;
        isFinish = true;
    }
}