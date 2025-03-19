package com.nro.nro_online.models.boss.robotsatthu;

import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.TaskService;

/**
 *
 * @Build by Arriety
 */
public class KingKong extends Boss {

    public KingKong() {
        super(BossFactory.KINGKONG, BossData.KINGKONG);
    }

    @Override
    public void joinMap() {
        super.joinMap();
        BossFactory.createBoss(BossFactory.POC).zone = this.zone;
        BossFactory.createBoss(BossFactory.PIC).zone = this.zone;
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        TaskService.gI().checkDoneTaskKillBoss(pl, this);
        generalRewards(pl);
    }

    @Override
    public void idle() {
    }

    @Override
    public void checkPlayerDie(Player pl) {
    }

    @Override
    public void initTalk() {
    }

}
