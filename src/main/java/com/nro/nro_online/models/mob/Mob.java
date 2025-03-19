package com.nro.nro_online.models.mob;

import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.consts.ConstMap;
import com.nro.nro_online.consts.ConstMob;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.map.dungeon.zones.ZSnakeRoad;
import com.nro.nro_online.models.player.Location;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.power.CaptionManager;
import com.nro.nro_online.services.MobService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.TaskService;
import com.nro.nro_online.utils.Util;

public class Mob {

    public int id;
    public Zone zone;
    public int tempId;
    public String name;
    public byte level;

    public MobPoint point;
    public MobEffectSkill effectSkill;
    public Location location;

    public byte pDame;
    public int pTiemNang;
    private long maxTiemNang;

    public long lastTimeDie;
    public int sieuquai = 0;

    public boolean actived;


    public Mob(Mob mob) {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
        this.id = mob.id;
        this.tempId = mob.tempId;
        this.level = mob.level;
        this.point.setHpFull(mob.point.getHpFull());
        this.point.setHP(this.point.getHpFull());
        this.location.x = mob.location.x;
        this.location.y = mob.location.y;
        this.pDame = mob.pDame;
        this.pTiemNang = mob.pTiemNang;
        this.setTiemNang();
        this.status = 5;
    }

    public Mob() {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
    }

    public int getSys() {
        return 0;
    }

    public void setTiemNang() {
        this.maxTiemNang = (long) this.point.getHpFull() * (this.pTiemNang + Util.nextInt(-2, 2)) / 100;
    }

    public byte status;
    protected long lastTimeAttackPlayer;

    public boolean isDie() {
        return this.point.getHP() <= 0;
    }

    public synchronized void injured(Player plAtt, int damage, boolean dieWhenHpFull) {
        if (!this.isDie()) {
            if (plAtt != null) {
            }

            if (damage >= this.point.hp) {
                damage = this.point.hp;
            }
            if (!dieWhenHpFull) {
                if (this.point.hp == this.point.maxHp && damage >= this.point.hp) {
                    damage = this.point.hp - 1;
                }
                if (this.tempId == 0 && damage > 10) {
                    damage = 10;
                }
            }
            this.point.hp -= damage;
            if (this.isDie()) {
                MobService.gI().sendMobDieAffterAttacked(this, plAtt, damage);
                MobService.gI().dropItemTask(plAtt, this);
                TaskService.gI().checkDoneTaskKillMob(plAtt, this);
                TaskService.gI().checkDoneSideTaskKillMob(plAtt, this);
                setDie();
            } else {
                MobService.gI().sendMobStillAliveAffterAttacked(this, damage, plAtt != null ? plAtt.nPoint.isCrit : false, plAtt);
            }
            if (plAtt != null) {
                Service.getInstance().addSMTN(plAtt, (byte) 2, getTiemNangForPlayer(plAtt, damage), true);
            }
        }
    }

    public long getTiemNangForPlayer(Player pl, long dame) {
        int levelPlayer = CaptionManager.getInstance().getLevel(pl);
        int n = levelPlayer - this.level;
        long pDameHit = dame * 100 / point.getHpFull();
        long tiemNang = pDameHit * maxTiemNang / 100;
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        if (n >= 0) {
            for (int i = 0; i < n; i++) {
                long sub = tiemNang * 15 / 100;
                if (sub <= 0) {
                    sub = 1;
                }
                tiemNang -= sub;
            }
        } else {
            for (int i = 0; i < -n; i++) {
                long add = tiemNang * 10 / 100;
                if (add <= 0) {
                    add = 1;
                }
                tiemNang += add;
            }
        }
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        tiemNang = (int) pl.nPoint.calSucManhTiemNang(tiemNang);
        return tiemNang;
    }

    public void update() {
        if (this.isDie()) {
            if (!(zone instanceof ZSnakeRoad)) {
                if ((zone.map.type == ConstMap.MAP_NORMAL
                        || zone.map.type == ConstMap.MAP_OFFLINE
                        || zone.map.type == ConstMap.MAP_BLACK_BALL_WAR) && (tempId != ConstMob.HIRUDEGARN) && Util.canDoWithTime(lastTimeDie, 2000)) {
                    MobService.gI().hoiSinhMob(this);
                } else if (this.zone.map.type == ConstMap.MAP_DOANH_TRAI && Util.canDoWithTime(lastTimeDie, 10000)) {
                    MobService.gI().hoiSinhMobDoanhTrai(this);
                }
            }
            return;
        }
        if (zone != null) {
            effectSkill.update();
            if (!zone.getPlayers().isEmpty() && Util.canDoWithTime(lastTimeAttackPlayer, 2000)) {
                attackPlayer();
            }
        }
    }

    public void attackPlayer() {
        if (!isDie() && !effectSkill.isHaveEffectSkill() && !(tempId == 0)) {
            Player pl = getPlayerCanAttack();
            if (pl != null) {
                int damage = MobService.gI().mobAttackPlayer(this, pl);
                MobService.gI().sendMobAttackMe(this, pl, damage);
                MobService.gI().sendMobAttackPlayer(this, pl);
            }
            this.lastTimeAttackPlayer = System.currentTimeMillis();
        }
    }

    public List<Player> playerAttackList = new ArrayList<>();

    public void addPlayerAttack(Player player) {
        if (player != null && !this.isDie() && !player.isBoss && !player.isMiniPet) {
            if (!playerAttackList.contains(player)) {
                this.playerAttackList.add(player);
            }
        } else {
            this.playerAttackList.clear();
        }
    }

    public Player getPlayerCanAttack() {
        int distance;
        Player plAttack = null;
        distance = 100;
        try {
            List<Player> players = this.zone.getNotBosses();
            for (Player pl : players) {
                if (!pl.isDie() && !pl.isBoss && !pl.effectSkin.isVoHinh && !pl.isMiniPet && !pl.nPoint.buffDefenseSatellite) {
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
    public void setDie() {
        this.lastTimeDie = System.currentTimeMillis();
    }
}
