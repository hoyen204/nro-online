package com.nro.nro_online.models.mob;

import com.nro.nro_online.consts.Cmd;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.SkillUtil;
import com.nro.nro_online.utils.Util;

public final class MobMe extends Mob {

    private Player player;
    private final long lastTimeSpawn;
    private final int timeSurvive;

    public MobMe(Player player) {
        super();
        this.player = player;
        this.id = (int) player.id;
        int level = player.playerSkill.getSkillbyId(12).point;
        this.tempId = SkillUtil.getTempMobMe(level);
        this.point.maxHp = SkillUtil.getHPMobMe(player.nPoint.hpMax, level);
        this.point.dame = SkillUtil.getHPMobMe(player.nPoint.getDameAttack(false), level);
        if (this.player.setClothes.pikkoroDaimao2 == 5) {
            this.point.dame *= 2;
        }
        if (this.player.setClothes.pikkoroDaimao1 == 5) {
            this.point.dame += ((long) this.point.dame * 50 / 100);
        }
        this.point.hp = this.point.maxHp;
        this.zone = player.zone;
        this.lastTimeSpawn = System.currentTimeMillis();
        this.timeSurvive = SkillUtil.getTimeSurviveMobMe(level);
        spawn();
    }

    @Override
    public void update() {
        if (Util.canDoWithTime(lastTimeSpawn, timeSurvive) && this.player.setClothes.pikkoroDaimao2 != 5) {
            this.mobMeDie();
            this.dispose();
        }
    }

    public void attack(Player pl, Mob mob) {
        if (pl != null && pl.nPoint.hp > this.point.dame) {
            try (Message msg = new Message(Cmd.MOB_ME_UPDATE)) {
                int dameHit = pl.injured(null, this.point.dame, true, true);
                msg.writer().writeByte(2);
                msg.writer().writeInt(this.id);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeInt(dameHit);
                msg.writer().writeInt(pl.nPoint.hp);
                Service.getInstance().sendMessAllPlayerInMap(this.zone, msg);
            } catch (Exception ignored) {
            }
        }

        if (mob != null && mob.point.getHP() > this.point.dame) {
            try (Message msg = new Message(Cmd.MOB_ME_UPDATE)) {
                long tnsm = mob.getTiemNangForPlayer(this.player, this.point.dame);
                msg.writer().writeByte(3);
                msg.writer().writeInt(this.id);
                msg.writer().writeInt((int) mob.id);
                mob.point.setHP(mob.point.getHP() - this.point.dame);
                msg.writer().writeInt(mob.point.getHP());
                msg.writer().writeInt(this.point.dame);
                Service.getInstance().sendMessAllPlayerInMap(this.zone, msg);
                Service.getInstance().addSMTN(player, (byte) 2, tnsm, true);
            } catch (Exception ignored) {
            }
        }
    }

    //tạo mobme
    public void spawn() {
        try (Message msg = new Message(Cmd.MOB_ME_UPDATE)) {
            msg.writer().writeByte(0);//type
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(this.tempId);
            msg.writer().writeInt(this.point.hp);// hp mob
            Service.getInstance().sendMessAllPlayerInMap(this.zone, msg);
        } catch (Exception e) {

        }
    }

    public void goToMap(Zone zone) {
        if (zone != null) {
            this.removeMobInMap();
            this.zone = zone;
        }
    }

    //xóa mobme khỏi map
    private void removeMobInMap() {
        try (Message msg = new Message(Cmd.MOB_ME_UPDATE)) {
            msg.writer().writeByte(7);//type
            msg.writer().writeInt((int) player.id);
            Service.getInstance().sendMessAllPlayerInMap(this.zone, msg);
        } catch (Exception e) {
        }
    }

    public void mobMeDie() {
        try (Message msg = new Message(Cmd.MOB_ME_UPDATE)) {
            msg.writer().writeByte(6);//type
            msg.writer().writeInt((int) player.id);
            Service.getInstance().sendMessAllPlayerInMap(this.zone, msg);
        } catch (Exception e) {
        }
    }

    public void dispose() {
        player.mobMe = null;
        this.player = null;
    }
}
