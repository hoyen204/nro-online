package com.nro.nro_online.models.boss.event;

import com.nro.nro_online.consts.ConstItem;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossManager;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Util;

public class Beetle extends EscortedBoss {

    private static final int FIXED_DAMAGE = 46;
    private static final int DROP_ITEM_Y_OFFSET = 100;
    private long lastSpecialSkillTime; // Thời gian dùng skill đặc biệt

    public Beetle(byte id, BossData data, Player owner) {
        super(id, data);
        setEscort(owner);
        joinMap();
        lastSpecialSkillTime = System.currentTimeMillis();
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        return super.injured(plAtt, FIXED_DAMAGE, piercing, isMobAttack);
    }

    @Override
    protected boolean useSpecialSkill() {
        if (Util.canDoWithTime(lastSpecialSkillTime, 15000)) {
            Service.getInstance().sendThongBao(escort, "Bọ Cánh Cứng hồi máu cho bạn nè! 😘");
            escort.nPoint.setHp(escort.nPoint.hp + 500);
            lastSpecialSkillTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    @Override
    public void rewards(Player pl) {
        if (this.zone == null) return;
        ItemMap itemMap = new ItemMap(
                this.zone,
                ConstItem.NGAI_DEM,
                1,
                this.location.x,
                this.zone.map.yPhysicInTop(this.location.x, DROP_ITEM_Y_OFFSET),
                -1
        );
        Service.getInstance().dropItemMap(this.zone, itemMap);
        Service.getInstance().sendThongBao(pl, "Bạn nhận được Ngải Đêm, ngon lành cành đào! 😜");
    }

    @Override
    public void joinMap() {
        if (escort == null || escort.zone == null) return;
        ChangeMapService.gI().changeMapBySpaceShip(this, escort.zone, ChangeMapService.NON_SPACE_SHIP);
    }

    @Override
    public void die() {
        if (this.isDie()) return;
        rewards(escort);
        leaveMap();
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

    @Override
    public void initTalk() {
        this.textTalkMidle = new String[]{
                "Tui là Bọ Cánh Cứng, đừng đập tui mạnh quá nha! 😭",
                "Escort ơi, đi chậm thôi kẻo tui lạc mất! 😅",
                "Đánh tui làm chi, tui chỉ muốn bay lượn thôi mà!"
        };
    }

    @Override
    public void idle() {
        if (escort != null && Util.getDistance(this, escort) > 50) {
            // Di chuyển ngẫu nhiên gần escort
            this.location.x = escort.location.x + Util.nextInt(-20, 20);
            this.location.y = escort.location.y + Util.nextInt(-10, 10);
        }
    }

    @Override
    public void checkPlayerDie(Player pl) {
        if (pl.isDie() && pl.equals(escort)) {
            Service.getInstance().sendThongBaoAllPlayer("Bọ Cánh Cứng bỏ trốn vì chủ nhân đã ngỏm! 😂");
            leaveMap();
        }
    }
}