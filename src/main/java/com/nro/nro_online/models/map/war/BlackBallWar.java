package com.nro.nro_online.models.map.war;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.map.Map;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.TimeUtil;
import com.nro.nro_online.utils.Util;

public class BlackBallWar {

    private static final int TIME_CAN_PICK_BLACK_BALL_AFTER_DROP = 5000;
    private static final int TIME_WIN = 300000;
    private static final int TIME_NOTIFY = 10000;

    public static final byte X3 = 3;
    public static final byte X5 = 5;
    public static final byte X7 = 7;

    public static final int COST_X3 = 100000000;
    public static final int COST_X5 = 300000000;
    public static final int COST_X7 = 500000000;

    public static final byte HOUR_OPEN = 20;
    public static final byte MIN_OPEN = 0;
    public static final byte SECOND_OPEN = 0;

    public static final byte HOUR_CAN_PICK_DB = 20;
    public static final byte MIN_CAN_PICK_DB = 30;
    public static final byte SECOND_CAN_PICK_DB = 0;

    public static final byte HOUR_CLOSE = 21;
    public static final byte MIN_CLOSE = 0;
    public static final byte SECOND_CLOSE = 0;
    public static final byte ZONES = 7;

    private static BlackBallWar instance;

    public static LocalDateTime TIME_OPEN;
    private static LocalDateTime TIME_CAN_PICK_DB;
    public static LocalDateTime TIME_CLOSE;

    private int day = -1;
    private final List<Map> maps;

    private BlackBallWar() {
        this.maps = new ArrayList<>();
    }

    public static BlackBallWar gI() {
        if (instance == null) {
            instance = new BlackBallWar();
        }
        instance.setTime();
        return instance;
    }

    private void setTime() {
        if (day == -1 || day != TimeUtil.getCurrDay()) {
            day = TimeUtil.getCurrDay();
            try {
                String dateNow = TimeUtil.getTimeNow("dd/MM/yyyy");
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                LocalDate currentDate = LocalDate.parse(dateNow, dateFormatter);

                TIME_OPEN = LocalDateTime.of(currentDate, LocalTime.of(HOUR_OPEN, MIN_OPEN, SECOND_OPEN));
                TIME_CAN_PICK_DB = LocalDateTime.of(currentDate, LocalTime.of(HOUR_CAN_PICK_DB, MIN_CAN_PICK_DB, SECOND_CAN_PICK_DB));
                TIME_CLOSE = LocalDateTime.of(currentDate, LocalTime.of(HOUR_CLOSE, MIN_CLOSE, SECOND_CLOSE));

            } catch (Exception e) {
                Log.error(BlackBallWar.class, e, "Error setting time for BlackBallWar");
            }
        }
    }

    public static LocalDateTime getTime(String time, String format) {
        return TimeUtil.getTime(time, format);
    }

    public void addMap(Map map) {
        maps.add(map);
    }

    public void dropBlackBall(Player player) {
        if (player.isHoldBlackBall) {
            ItemMap itemMap = new ItemMap(player.zone, player.tempIdBlackBallHold, 1, player.location.x,
                    player.zone.map.yPhysicInTop(player.location.x, player.location.y - 24), -1);
            Service.getInstance().dropItemMap(itemMap.zone, itemMap);
            player.isHoldBlackBall = false;
            player.tempIdBlackBallHold = -1;
            player.zone.lastTimeDropBlackBall = System.currentTimeMillis();
            Service.getInstance().sendFlagBag(player);
            updateClanFlags(player);
        }
    }

    private void updateClanFlags(Player player) {
        if (player.clan != null) {
            List<Player> players = player.zone.getPlayers();
            synchronized (players) {
                for (Player pl : players) {
                    if (pl.clan != null && player.clan.equals(pl.clan)) {
                        Service.getInstance().changeFlag(pl, Util.nextInt(1, 7));
                    }
                }
            }
        } else {
            Service.getInstance().changeFlag(player, Util.nextInt(1, 7));
        }
    }

    public void update(Player player) {
        if (player.isHoldBlackBall) {
            long currentTime = System.currentTimeMillis();
            if (Util.canDoWithTime(player.lastTimeHoldBlackBall, TIME_WIN)) {
                win(player);
            } else if (Util.canDoWithTime(player.lastTimeNotifyTimeHoldBlackBall, TIME_NOTIFY)) {
                notifyHoldTime(player, currentTime);
            }
        }
        checkMapTime(player);
    }

    private void notifyHoldTime(Player player, long currentTime) {
        Service.getInstance().sendThongBao(player, "Cố gắng giữ ngọc rồng trong "
                + TimeUtil.getSecondLeft(player.lastTimeHoldBlackBall, TIME_WIN / 1000)
                + " giây nữa, đem chiến thắng về cho bang hội!");
        player.lastTimeNotifyTimeHoldBlackBall = currentTime;
    }

    public boolean isOnBlackBallWarTime() {
        LocalDateTime now = LocalDateTime.now();
        return (now.isAfter(TIME_OPEN) && now.isBefore(TIME_CLOSE));
    }

    public void checkMapTime(Player player) {
        try {
            if (player.zone != null && player.zone.map != null && isInWarMap(player.zone.map.mapId)) {

                if (isOnBlackBallWarTime()) {
                    return;
                }
                if (player.isHoldBlackBall) {
                    win(player);
                } else {
                    kickOutOfMap(player);
                }
            }
        } catch (Exception ex) {
            Log.error(BlackBallWar.class, ex, "Error checking map time");
            Service.getInstance().sendThongBaoFromAdmin(player, "Đã xảy ra lỗi, vui lòng báo Admin!");
        }
    }

    private static boolean isInWarMap(int mapId) {
        return mapId >= 85 && mapId <= 91;
    }

    private void win(Player player) {
        player.zone.finishBlackBallWar = true;
        int star = player.tempIdBlackBallHold - 371;
        rewardClan(player, star);
        kickAllPlayersOut(player.zone.getPlayers());
    }

    private void rewardClan(Player player, int star) {
        List<Player> recipients = (player.clan != null) ? player.clan.membersInGame : List.of(player);
        try {
            for (Player pl : recipients) {
                if (pl != null) {
                    pl.rewardBlackBall.reward((byte) star);
                    Service.getInstance().sendThongBao(pl, "Chúc mừng bang hội của bạn đã dành chiến thắng ngọc rồng sao đen " + star + " sao");
                }
            }
        } catch (Exception e) {
            Log.error(BlackBallWar.class, e, "Error rewarding clan for BlackBallWar");
        }
    }

    private void kickAllPlayersOut(List<Player> players) {
        players.forEach(this::kickOutOfMap);
    }

    private void kickOutOfMap(Player player) {
        if (player.cFlag == 8) {
            Service.getInstance().changeFlag(player, Util.nextInt(1, 7));
        }
        Service.getInstance().sendThongBao(player, "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
        ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
    }

    public void changeMap(Player player, byte index) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(TIME_OPEN) && now.isBefore(TIME_CLOSE)) {
            ChangeMapService.gI().changeMap(player, player.mapBlackBall.get(index).map.mapId, -1, 50, 50);
        } else {
            Service.getInstance().sendThongBao(player, "Đại chiến ngọc rồng đen chưa mở");
            Service.getInstance().hideWaitDialog(player);
        }
    }

    public void joinMapBlackBallWar(Player player) {
        boolean changed = false;
        if (player.clan != null) {
            for (Player pl : player.zone.getPlayers()) {
                if (pl.clan != null && !player.equals(pl) && player.clan.equals(pl.clan)) {
                    Service.getInstance().changeFlag(player, pl.cFlag);
                    changed = true;
                    break;
                }
            }
        }
        if (!changed) {
            Service.getInstance().changeFlag(player, Util.nextInt(1, 7));
        }
    }

    public boolean pickBlackBall(Player player, Item item) {
        try {
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(TIME_CAN_PICK_DB)) {
                notifyPickTime(player);
                return false;
            } else if (player.zone.finishBlackBallWar) {
                Service.getInstance().sendThongBao(player, "Đại chiến ngọc rồng sao đen đã kết thúc, vui lòng đợi đến ngày mai");
                return false;
            } else if (Util.canDoWithTime(player.zone.lastTimeDropBlackBall, TIME_CAN_PICK_BLACK_BALL_AFTER_DROP)) {
                holdBlackBall(player, item);
                return true;
            } else {
                Service.getInstance().sendThongBao(player, "Không thể nhặt ngọc rồng đen ngay lúc này");
                return false;
            }
        } catch (Exception ex) {
            Log.error(BlackBallWar.class, ex, "Error picking black ball");
            return false;
        }
    }

    private void notifyPickTime(Player player) {
        long secondsLeft = ChronoUnit.SECONDS.between(LocalDateTime.now(), TIME_CAN_PICK_DB);
        Service.getInstance().sendThongBao(player, "Chưa thể nhặt ngọc rồng ngay lúc này, vui lòng đợi "
                + secondsLeft + " giây nữa");
    }

    private void holdBlackBall(Player player, Item item) {
        player.isHoldBlackBall = true;
        player.tempIdBlackBallHold = item.template.id;
        player.lastTimeHoldBlackBall = System.currentTimeMillis();
        Service.getInstance().sendFlagBag(player);
        updateClanFlags(player);
    }

    public void xHPKI(Player player, byte x) {
        int cost = getCost(x);
        if (player.inventory.gold >= cost) {
            applyXHPKI(player, x, cost);
        } else {
            notifyInsufficientGold(player, cost);
        }
    }

    private int getCost(byte x) {
        return switch (x) {
            case X3 -> COST_X3;
            case X5 -> COST_X5;
            case X7 -> COST_X7;
            default -> 0;
        };
    }

    private void applyXHPKI(Player player, byte x, int cost) {
        player.inventory.gold -= cost;
        Service.getInstance().sendMoney(player);
        player.effectSkin.lastTimeXHPKI = System.currentTimeMillis();
        player.effectSkin.xHPKI = x;
        player.nPoint.calPoint();
        player.nPoint.setHp((long) player.nPoint.hp * x);
        player.nPoint.setMp((long) player.nPoint.mp * x);
        PlayerService.gI().sendInfoHpMp(player);
        Service.getInstance().point(player);
    }

    private void notifyInsufficientGold(Player player, int cost) {
        Service.getInstance().sendThongBao(player, "Không đủ vàng để thực hiện, còn thiếu "
                + Util.numberToMoney(cost - player.inventory.gold) + " vàng");
    }
}
