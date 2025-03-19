package com.nro.nro_online.models.boss.boss_ban_do_kho_bau;

import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossManager;
import com.nro.nro_online.models.map.phoban.BanDoKhoBau;
import com.nro.nro_online.models.mob.Mob;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Util;

public abstract class BossBanDoKhoBau extends Boss {
    private static final int JOIN_X = 1065;
    protected final BanDoKhoBau banDoKhoBau;

    public BossBanDoKhoBau(byte id, BossData data, BanDoKhoBau banDoKhoBau) {
        super(id, data);
        this.banDoKhoBau = banDoKhoBau;
        spawn(banDoKhoBau.level);
    }

    private void spawn(byte level) {
        this.nPoint.hpg = level * this.data.hp[0][0];
        this.nPoint.dameg = calculateDamage();
        this.nPoint.calPoint();
        this.nPoint.setFullHpMp();
    }

    private int calculateDamage() {
        return switch (this.data.typeDame) {
            case DAME_PERCENT_HP_THOU -> this.nPoint.hpg / 1000 * this.data.dame;
            case DAME_PERCENT_HP_HUND -> this.nPoint.hpg / 100 * this.data.dame;
            default -> 0;
        };
    }

    @Override
    public void attack() {
        super.attack();
    }

    @Override
    public void idle() {
        boolean allMobDie = true;
        for (Mob mob : this.zone.mobs) {
            if (!mob.isDie()) {
                allMobDie = false;
                break;
            }
        }
        if (allMobDie) {
            this.changeToAttack();
        }
    }

    @Override
    public void checkPlayerDie(Player pl) {
        if (pl.isDie()) {
            Service.getInstance().chat(this, "Chừa chưa ranh con, nên nhớ ta là " + this.name);
        }
    }

    @Override
    public void initTalk() {}

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

    @Override
    public void rewards(Player pl) {
        dropItemReward(2040, (int) pl.id);
        dropItemReward(2040, (int) pl.id);
        dropItemReward(2011, (int) pl.id);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    protected void notifyPlayeKill(Player player) {}

    @Override
    public void joinMap() {
        if (this.mapJoin == null || this.mapJoin.length == 0) return;
        this.zone = this.banDoKhoBau.getMapById(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
        if (this.zone != null) {
            ChangeMapService.gI().changeMap(this, this.zone, JOIN_X, this.zone.map.yPhysicInTop(JOIN_X, 0));
        }
    }
}