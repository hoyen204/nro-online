package com.nro.nro_online.models.mob;

import java.util.List;

import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.utils.Util;

public abstract class BigBoss extends Mob implements IBigBoss {

    public BigBoss(Mob mob) {
        super(mob);
    }

    @Override
    public void move(int x, int y) {
        location.x = x;
        location.y = y;
    }

    @Override
    public Player getPlayerCanAttack() {
        int distance = 1000;
        Player plAttack = null;
        try {
            List<Player> players = this.zone.getNotBosses();
            for (Player pl : players) {
                if (!pl.isDie() && !pl.isBoss && !pl.effectSkin.isVoHinh && !pl.isMiniPet) {
                    int dis = Util.getDistance(pl, this);
                    if (dis <= distance) {
                        plAttack = pl;
                        distance = dis;
                    }
                }
            }
        } catch (Exception e) {

        }
        return plAttack;
    }

    @Override
    public void attackPlayer() {
        if (!isDie() && !effectSkill.isHaveEffectSkill() && !(tempId == 0) && Util.canDoWithTime(lastTimeAttackPlayer, 2000)) {
            Player pl = getPlayerCanAttack();
            if (pl != null) {
                attack(pl);
            }
            this.lastTimeAttackPlayer = System.currentTimeMillis();
        }
    }

}
