/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nro.nro_online.models.mob;

import java.io.IOException;

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
public class GuardRobot extends BigBoss {

    public GuardRobot(Mob mob) {
        super(mob);
    }

    @Override
    public void attack(Player target) {
        byte action = (byte) Util.nextInt(3);
        if (action != 1 && target.location.y != 336) {
            action = 1;
        }
        if (action == 0 || action == 2) {
            location.x += (target.location.x - location.x) / 4;
        }
        if (action == 0) {
            location.y += (target.location.y - location.y) / 4;
        }
        int damage = MobService.gI().mobAttackPlayer(this, target);
        send(target, damage, action);
    }

    @Override
    public void setDie() {
        super.setDie();
        Util.setTimeout(() -> {
            hide();
        }, 3000);
    }

    public void send(Player cAttack, int damage, byte type) {
        try (Message ms = new Message(Cmd.BIG_BOSS_2)) {
            ms.writer().writeByte(type);
            ms.writer().writeByte(1);
            ms.writer().writeInt((int) cAttack.id);
            ms.writer().writeInt(damage);
            Service.getInstance().sendMessAllPlayerInMap(zone, ms);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void hide() {
        move(-1000, -1000);
        try (Message ms = new Message(Cmd.BIG_BOSS_2)) {
            ms.writer().writeByte(6);
            Service.getInstance().sendMessAllPlayerInMap(zone, ms);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
