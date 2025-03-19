package com.nro.nro_online.models.map.mabu;

import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.TimeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MabuWar14h {
    private static MabuWar14h instance;
    public final List<Boss> bosses = new ArrayList<>();
    private static LocalDateTime TIME_OPEN;
    private static LocalDateTime TIME_CLOSE;
    private static final byte HOUR_OPEN = 2;   // 2h sáng, giờ ma quỷ bắt đầu 😜
    private static final byte MIN_OPEN = 0;
    private static final byte HOUR_CLOSE = 3;  // 3h sáng, hết giờ chơi!
    private static final byte MIN_CLOSE = 0;


    private int day = -1; // Ngày hiện tại, -1 là chưa set
    private boolean initBoss = false; // Đã spawn boss chưa?
    private boolean clearBoss = false; // Đã dọn boss chưa?

    public static MabuWar14h gI() {
        if (instance == null) {
            instance = new MabuWar14h();
        }
        instance.setTime(); // Set thời gian mỗi khi gọi, chắc ăn! 😎
        return instance;
    }

    private void setTime() {
        if (day == -1 || day != TimeUtil.getCurrDay()) {
            day = TimeUtil.getCurrDay();
            try {
                String today = TimeUtil.getTimeNow("dd/MM/yyyy");
                TIME_OPEN = TimeUtil.getTime(today + " " + HOUR_OPEN + ":" + MIN_OPEN + ":0", "dd/MM/yyyy HH:mm:ss");
                TIME_CLOSE = TimeUtil.getTime(today + " " + HOUR_CLOSE + ":" + MIN_CLOSE + ":0", "dd/MM/yyyy HH:mm:ss");
            } catch (Exception e) {
                e.printStackTrace(); // Lỗi set thời gian thì kệ, in ra cho vui 😂
            }
        }
    }

    public boolean isTimeMabuWar() {
        LocalDateTime now = LocalDateTime.now();
        return TIME_OPEN.isBefore(now) && TIME_CLOSE.isAfter(now); // Đúng giờ thì chiến, sai giờ thì nghỉ! ⚡
    }

    public void update(Player player) {
        if (player == null || player.zone == null) return; // Player null thì nghỉ, khỏi làm gì 😅

        if (!MapService.gI().isMapMabuWar14H(player.zone.map.mapId)) return; // Sai map thì bye bye! 👋

        try {
            if (isTimeMabuWar()) {
                if (!initBoss) {
                    BossFactory.initBossMabuWar14H(); // Spawn boss, giờ chiến đấu bắt đầu! 🚀
                    initBoss = true;
                }
            } else if (!MabuWar.gI().isTimeMabuWar()) {
                kickOutOfMap(player); // Hết giờ, về nhà thôi bro!
                removeAllBoss(); // Dọn boss cho sạch sẽ 😛
            }
        } catch (Exception e) {
            System.out.println("Bug player: " + player.name + " - Lỗi rồi bro!");
            Service.getInstance().sendThongBao(player, "Có lỗi xảy ra, chịu khó tí nha! 😢");
            e.printStackTrace();
        }
    }

    private void kickOutOfMap(Player player) {
        Service.getInstance().sendThongBao(player, "Đại chiến kết thúc, về nhà bằng tàu nha bro!");
        ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
    }

    public void removeAllBoss() {
        if (clearBoss) return; // Đã dọn rồi thì thôi, khỏi làm lại 😅

        bosses.forEach(Boss::leaveMap); // Tiễn boss đi hết, bye bye! 👋
        bosses.clear();
        clearBoss = true; // Đánh dấu đã dọn, xong việc rồi! 🎉
    }
}