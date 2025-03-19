package com.nro.nro_online.models.boss.robotsatthu;

import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.boss.BossManager;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.TaskService;

/**
 *
 * @Build by Arriety
 */
public class Pic extends Boss {

    public Pic() {
        super(BossFactory.PIC, BossData.PIC);
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

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        BossManager.gI().getBossById(BossFactory.KINGKONG).changeToAttack();
    }
}
