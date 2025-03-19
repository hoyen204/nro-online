/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nro.nro_online.models.mob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.nro.nro_online.consts.Cmd;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.MobService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Util;

/**
 *
 * @Build by Arriety
 */
public class Hirudegarn extends BigBoss {

    private byte type;
    private long lastTimeMove;

    public Hirudegarn(Mob mob) {
        super(mob);
        this.point.dame = 100000;
    }

    @Override
    public int getSys() {
        return type;
    }

    public List<Player> getListPlayerCanAttack(int range) {
        List<Player> list = new ArrayList<>();
        try {
            List<Player> players = this.zone.getNotBosses();
            for (Player pl : players) {
                if (!pl.isDie() && !pl.isBoss && !pl.effectSkin.isVoHinh && !pl.isMiniPet) {
                    int dis = Util.getDistance(pl, this);
                    if (dis <= range) {
                        list.add(pl);
                    }
                }
            }
        } catch (Exception e) {

        }
        return list;
    }

    private void jump() {
        if (!isDie() && !effectSkill.isHaveEffectSkill()) {
            List<Player> players = getListPlayerCanAttack(500);
            int[][] array = new int[players.size()][2];
            int i = 0;
            for (Player pl : players) {
                int damage = MobService.gI().mobAttackPlayer(this, pl);
                array[i][0] = (int) pl.id;
                array[i][1] = damage;
                i++;
            }
            send(array, (byte) 2);
            this.lastTimeAttackPlayer = System.currentTimeMillis();
        }
    }

    public void send(int[][] array, byte type) {
        try (Message ms = new Message(Cmd.BIG_BOSS)) {
            ms.writer().writeByte(type);
            ms.writer().writeByte(array.length);
            for (int[] arr : array) {
                ms.writer().writeInt(arr[0]);
                ms.writer().writeInt(arr[1]);
            }
            Service.getInstance().sendMessAllPlayerInMap(zone, ms);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void attackPlayer() {
        if (Util.canDoWithTime(lastTimeAttackPlayer, 2000)) {
            int rd = Util.nextInt(5);
            if (rd == 0) {
                jump();
            } else if (rd == 1) {
                flyTo(Util.nextInt(30, zone.map.mapWidth - 30), 360);
            } else {
                super.attackPlayer();
            }
        }
        if (Util.canDoWithTime(lastTimeMove, 2000)) {
            int rd = Util.nextInt(3);
            if (rd == 0) {
                move(Util.nextInt(30, zone.map.mapWidth - 30), 360);
            }
            lastTimeMove = System.currentTimeMillis();
        }
    }

    @Override
    public void attack(Player target) {
        byte action = (byte) ((byte) Util.nextInt(4) == 3 ? 1 : 0);
        int damage = MobService.gI().mobAttackPlayer(this, target);
        send(target, damage, action);
    }

    public void send(Player cAttack, int damage, byte type) {
        try (Message ms = new Message(Cmd.BIG_BOSS)) {
            ms.writer().writeByte(type);
            ms.writer().writeByte(1);
            ms.writer().writeInt((int) cAttack.id);
            ms.writer().writeInt(damage);
            Service.getInstance().sendMessAllPlayerInMap(zone, ms);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void transform() {
        try (Message ms = new Message(Cmd.BIG_BOSS)) {
            this.type++;
            if (this.type <= 2) {
                MobService.gI().hoiSinhMob(this);
            }
            if (type == 1) {
                ms.writer().writeByte(6);
                ms.writer().writeShort(location.x);
                ms.writer().writeShort(location.y);
            } else if (type == 2) {
                ms.writer().writeByte(5);
            } else {
                super.move(-1000, -1000);
                ms.writer().writeByte(9);
            }
            Service.getInstance().sendMessAllPlayerInMap(zone, ms);
        } catch (IOException ex) {
            Logger.getLogger(Hirudegarn.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void flyTo(int x, int y) {
        try (Message ms = new Message(Cmd.BIG_BOSS)) {
            super.move(x, y);
            ms.writer().writeByte(3);
            ms.writer().writeShort(x);
            ms.writer().writeShort(y);
            Service.getInstance().sendMessAllPlayerInMap(zone, ms);
        } catch (IOException ex) {
            Logger.getLogger(Hirudegarn.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void move(int x, int y) {
        super.move(x, y);
        try (Message ms = new Message(Cmd.BIG_BOSS)) {
            ms.writer().writeByte(8);
            ms.writer().writeShort(x);
            ms.writer().writeShort(y);
            Service.getInstance().sendMessAllPlayerInMap(zone, ms);
        } catch (IOException ex) {
            Logger.getLogger(Hirudegarn.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setDie() {
        super.setDie();
        Util.setTimeout(() -> {
            transform();
        }, 3000);
    }

    @Override
    public synchronized void injured(Player plAtt, int damage, boolean dieWhenHpFull) {
        damage /= 2;
        int max = this.point.hp / ((this.type + 1) * 20);
        if (max <= 0) {
            max = 1;
        }
        if (damage > max) {
            damage = max;
        }
        super.injured(plAtt, damage, dieWhenHpFull);
    }

}
