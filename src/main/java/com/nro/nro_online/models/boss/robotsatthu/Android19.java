package com.nro.nro_online.models.boss.robotsatthu;

import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.boss.BossManager;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.TaskService;

/**
 *
 * @Build by Arriety
 */
public class Android19 extends Boss {

    public Android19() {
        super(BossFactory.ANDROID_19, BossData.ANDROID_19);
    }

    @Override
    public void joinMap() {
        super.joinMap();
    }

    @Override
    public void leaveMap() {
        BossManager.gI().getBossById(BossFactory.ANDROID_20).changeToAttack();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
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
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (plAtt != null) {
            switch (plAtt.playerSkill.skillSelect.template.id) {
                case Skill.KAMEJOKO:
                case Skill.MASENKO:
                case Skill.ANTOMIC:
                    int hpHoi = (int) (damage - ((long) damage * 80 / 100));
                    PlayerService.gI().hoiPhuc(this, hpHoi, 0);
                    return 0;
            }
        }
        return super.injured(plAtt, damage, piercing, isMobAttack);
    }

}
