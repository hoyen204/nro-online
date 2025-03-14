package com.nro.nro_online.models.boss.event;

import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.consts.ConstRatio;
import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Util;

public abstract class EscortedBoss extends Boss {

    protected Player escort;
    private static final int MIN_DISTANCE = 24; // Khoảng cách tối thiểu với escort
    private static final int MOVE_RANGE = 50; // Phạm vi di chuyển ngẫu nhiên

    public EscortedBoss(byte id, BossData data) {
        super(id, data);
    }

    @Override
    public void attack() {
        move(); // Gọi move thay vì attack, phù hợp với boss hộ tống
    }

    public void move() {
        if (!Util.isTrue(50, ConstRatio.PER100)) return; // 50% cơ hội di chuyển

        int x, y;
        if (escort == null) {
            // Di chuyển ngẫu nhiên khi không có escort
            x = location.x + Util.nextInt(-MOVE_RANGE, MOVE_RANGE);
            y = location.y;
        } else {
            int distance = Util.getDistance(this, escort);
            if (distance <= MIN_DISTANCE) return; // Đã gần thì không cần di chuyển

            // Di chuyển về phía escort
            x = location.x + (location.x < escort.location.x ? Util.nextInt(12, 36) : -Util.nextInt(12, 36));
            y = escort.location.y;
        }

        // Giới hạn biên map
        x = Math.max(35, Math.min(x, this.zone.map.mapWidth - 35));
        if (location.y > 50) {
            y = this.zone.map.yPhysicInTop(x, y - 50);
        }

        goToXY(x, y, false);
    }

    public void joinMapEscort() {
        if (escort == null || escort.zone == null) return; // Check null trước

        if (!MapService.gI().isMapVS(escort.zone.map.mapId)) {
            this.location.x = escort.location.x + Util.nextInt(-10, 10);
            this.location.y = escort.location.y;
            MapService.gI().goToMap(this, escort.zone);
            escort.zone.load_Me_To_Another(this);
        } else {
            stopEscorting();
            Service.getInstance().sendThongBao(escort, "Boss không thích map VS, bye bye! 😛");
        }
    }

    public void setEscort(Player escort) {
        if (this.escort != null) stopEscorting(); // Dừng hộ tống escort cũ nếu có
        this.escort = escort;
        if (escort != null) {
            escort.setEscortedBoss(this);
            PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.NON_PK);
            joinMapEscort(); // Tham gia map ngay khi set escort
        }
    }

    public void stopEscorting() {
        if (escort != null) {
            escort.setEscortedBoss(null);
            Service.getInstance().sendThongBao(escort, "Boss bỏ đi rồi, tự lo nha! 😂");
            escort = null;
        }
        changeStatus(LEAVE_MAP);
    }
}