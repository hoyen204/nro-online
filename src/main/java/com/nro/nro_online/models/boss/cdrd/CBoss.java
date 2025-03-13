/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nro.nro_online.models.boss.cdrd;

import com.nro.nro_online.consts.ConstMap;
import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.map.dungeon.SnakeRoad;
import com.nro.nro_online.models.map.dungeon.zones.ZSnakeRoad;
import com.nro.nro_online.models.player.Player;
import nro.consts.ConstMap;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.map.dungeon.SnakeRoad;
import nro.models.map.dungeon.zones.ZSnakeRoad;
import nro.models.player.Player;

/**
 *
 * @stole Arriety
 */
public abstract class CBoss extends Boss {

    protected SnakeRoad snakeRoad;
    private short px, py;

    public CBoss(long id, short x, short y, SnakeRoad dungeon, BossData data) {
        super((byte) 0, data);
        this.id = id;
        this.snakeRoad = dungeon;
        this.px = x;
        this.py = y;
    }

    @Override
    protected abstract boolean useSpecialSkill();

    @Override
    public abstract void rewards(Player pl);

    @Override
    public abstract void idle();

    @Override
    public abstract void checkPlayerDie(Player pl);

    @Override
    public abstract void initTalk();

    @Override
    public void joinMap() {
        zone = snakeRoad.find(ConstMap.HOANG_MAC);
        ((ZSnakeRoad) zone).enter(this, px, py);
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        snakeRoad.removeBoss(this);
        CBoss boss = snakeRoad.getBoss(0);
        if (boss != null) {
            boss.changeToAttack();
        }
    }

    @Override
    protected void notifyPlayeKill(Player player) {
    }

}
