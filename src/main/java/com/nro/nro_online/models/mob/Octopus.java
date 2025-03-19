package com.nro.nro_online.models.mob;

import com.nro.nro_online.consts.Cmd;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.MobService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Util;

import java.util.List;

public class Octopus extends BigBoss {

    public Octopus(Mob mob) {
        super(mob);
    }

    @Override
    public Player getPlayerCanAttack() {
        int distance = 500;
        Player plAttack = null;
        List<Player> players = zone.getNotBosses();
        for (Player pl : players) {
            if (!pl.isDie() && !pl.isBoss && !pl.effectSkin.isVoHinh && !pl.isMiniPet
                    && pl.location.x >= 442 && pl.location.x <= 960 && pl.location.y >= 400) {
                int dis = Util.getDistance(pl, this);
                if (dis <= distance) {
                    plAttack = pl;
                    distance = dis;
                }
            }
        }
        return plAttack;
    }

    @Override
    public void move(int x, int y) {
        super.move(x, y);
        if (x > 0 && y > 0)
            moveX((short) x);
    }

    public void hide() {
        move(-1000, -1000);
        try (Message ms = new Message(Cmd.BIG_BOSS_2)) {
            ms.writer().writeByte(7);
            Service.getInstance().sendMessAllPlayerInMap(zone, ms);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void send(Player cAttack, int damage, byte type) {
        try (Message ms = new Message(Cmd.BIG_BOSS_2)) {
            ms.writer().writeByte(type);
            ms.writer().writeByte(1);
            ms.writer().writeInt((int) cAttack.id);
            ms.writer().writeInt(damage);
            Service.getInstance().sendMessAllPlayerInMap(zone, ms);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void moveX(short x) {
        try (Message ms = new Message(Cmd.BIG_BOSS_2)) {
            ms.writer().writeByte(5);
            ms.writer().writeShort(x);
            Service.getInstance().sendMessAllPlayerInMap(zone, ms);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void attack(Player target) {
        byte action = (byte) Util.nextInt(3, 5);
        if ((action == 3 || action == 5) && target.location.y != 576)
            action = 4;
        if (action == 5) {
            move(target.location.x, 576);
            return;
        }
        if (action == 3) {
            location.x += (target.location.x - location.x) / 4;
            location.y += (target.location.y - location.y) / 4;
        }
        int damage = MobService.gI().mobAttackPlayer(this, target);
        send(target, damage, action);
    }

    @Override
    public void setDie() {
        super.setDie();
        hide();
    }
}