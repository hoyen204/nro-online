package com.nro.nro_online.models.boss.event;

import com.nro.nro_online.consts.ConstItem;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossManager;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Util;

public class NightLord extends EscortedBoss {

    private static final int FIXED_DAMAGE = 46; // Sát thương cố định
    private static final int DROP_Y_OFFSET = 100; // Độ cao drop item
    private long lastSpecialSkillTime; // Thời gian dùng skill đặc biệt

    public NightLord(byte id, BossData data, Player owner) {
        super(id, data);
        setEscort(owner);
        joinMap(); // Tham gia map ngay khi khởi tạo
        lastSpecialSkillTime = System.currentTimeMillis();
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        return super.injured(plAtt, FIXED_DAMAGE, piercing, isMobAttack); // Luôn nhận 46 damage
    }

    @Override
    protected boolean useSpecialSkill() {
        if (Util.canDoWithTime(lastSpecialSkillTime, 20000)) { // Cooldown 20 giây
            Service.getInstance().sendThongBao(escort, "Night Lord tăng tốc cho bạn, chạy lẹ lên! 😈");
            escort.nPoint.speed += 2; // Tăng tốc độ escort
            lastSpecialSkillTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    @Override
    public void rewards(Player pl) {
        if (this.zone == null) return; // Check null để an toàn
        ItemMap itemMap = new ItemMap(
                this.zone,
                ConstItem.BO_CANH_CUNG,
                1,
                this.location.x,
                this.zone.map.yPhysicInTop(this.location.x, DROP_Y_OFFSET),
                -1
        );
        Service.getInstance().dropItemMap(this.zone, itemMap);
        Service.getInstance().sendThongBao(pl, "Nhận được Bộ Cánh Cứng, bay cao nào! 😎");
    }

    @Override
    public void initTalk() {
        this.textTalkMidle = new String[]{
                "Ta là Night Lord, đừng hòng vượt qua ta dễ dàng! 😏",
                "Đi theo ta nếu muốn sống sót, nhóc! 😈",
                "Đừng để ta phải đợi lâu, ta không kiên nhẫn đâu!"
        };
    }

    @Override
    public void idle() {
        if (escort != null && Util.getDistance(this, escort) > 30) {
            // Di chuyển ngẫu nhiên quanh escort
            this.location.x = escort.location.x + Util.nextInt(-15, 15);
            this.location.y = escort.location.y + Util.nextInt(-10, 10);
        }
    }

    @Override
    public void checkPlayerDie(Player pl) {
        if (pl.isDie() && pl.equals(escort)) {
            Service.getInstance().sendThongBaoAllPlayer("Night Lord cười khẩy: 'Chủ yếu thế này thì ta đi đây!' 😂");
            leaveMap();
        }
    }

    @Override
    public void joinMap() {
        if (escort != null && escort.zone != null) {
            ChangeMapService.gI().changeMapBySpaceShip(this, escort.zone, ChangeMapService.NON_SPACE_SHIP);
        }
    }

    @Override
    public void die() {
        if (this.isDie()) return; // Tránh gọi die nhiều lần
        rewards(escort); // Drop phần thưởng trước khi rời
        leaveMap();
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }
}