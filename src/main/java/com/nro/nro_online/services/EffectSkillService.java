package com.nro.nro_online.services;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import com.nro.nro_online.models.mob.Mob;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.SkillUtil;

public class EffectSkillService {

    public static final byte TURN_ON_EFFECT = 1;
    public static final byte TURN_OFF_EFFECT = 0;
    public static final byte TURN_OFF_ALL_EFFECT = 2;
    public static final byte HOLD_EFFECT = 32;
    public static final byte SHIELD_EFFECT = 33;
    public static final byte HUYT_SAO_EFFECT = 39;
    public static final byte BLIND_EFFECT = 40;
    public static final byte SLEEP_EFFECT = 41;
    public static final byte STONE_EFFECT = 42;

    private static final EffectSkillService i = new EffectSkillService();
    private Player player;

    public static EffectSkillService gI() {
        return i;
    }

    public void sendEffectUseSkill(Player player, byte skillId) {
        Skill skill = SkillUtil.getSkillbyId(player, skillId);
        sendMessage(player, -45, msg -> {
            msg.writer().writeByte(8);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(skill.skillId);
            Service.getInstance().sendMessAnotherNotMeInMap(player, msg);
        });
    }

    public void sendEffectPlayer(Player plUseSkill, Player plTarget, byte toggle, byte effect) {
        sendMessage(plUseSkill, -124, msg -> {
            msg.writer().writeByte(toggle);
            msg.writer().writeByte(0);
            if (toggle == TURN_OFF_ALL_EFFECT) {
                msg.writer().writeInt((int) plTarget.id);
            } else {
                msg.writer().writeByte(effect);
                msg.writer().writeInt((int) plTarget.id);
                msg.writer().writeInt((int) plUseSkill.id);
            }
            Service.getInstance().sendMessAllPlayerInMap(plUseSkill, msg);
        });
    }

    public void sendEffectMob(Player plUseSkill, Mob mobTarget, byte toggle, byte effect) {
        sendMessage(mobTarget.zone, -124, msg -> {
            msg.writer().writeByte(toggle);
            msg.writer().writeByte(1);
            msg.writer().writeByte(effect);
            msg.writer().writeByte(mobTarget.id);
            msg.writer().writeInt((int) plUseSkill.id);
            Service.getInstance().sendMessAllPlayerInMap(mobTarget.zone, msg);
        });
    }

    public void removeUseTroi(Player player) {
        if (player.effectSkill.mobAnTroi != null) player.effectSkill.mobAnTroi.effectSkill.removeAnTroi();
        if (player.effectSkill.plAnTroi != null) removeAnTroi(player.effectSkill.plAnTroi);
        player.effectSkill.useTroi = false;
        player.effectSkill.mobAnTroi = null;
        player.effectSkill.plAnTroi = null;
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, HOLD_EFFECT);
    }

    private void removeAnTroi(Player player) {
        if (player != null && player.effectSkill != null) {
            player.effectSkill.anTroi = false;
            player.effectSkill.plTroi = null;
            sendEffectPlayer(player, player, TURN_OFF_EFFECT, HOLD_EFFECT);
        }
    }

    public void setAnTroi(Player player, Player plTroi, long lastTimeAnTroi, int timeAnTroi) {
        player.effectSkill.anTroi = true;
        player.effectSkill.plTroi = plTroi;
    }

    public void setUseTroi(Player player, long lastTimeTroi, int timeTroi) {
        player.effectSkill.useTroi = true;
        player.effectSkill.lastTimeTroi = lastTimeTroi;
        player.effectSkill.timeTroi = timeTroi;
    }

    public void setThoiMien(Player player, long lastTimeThoiMien, int timeThoiMien) {
        player.effectSkill.isThoiMien = true;
        player.effectSkill.lastTimeThoiMien = lastTimeThoiMien;
        player.effectSkill.timeThoiMien = timeThoiMien;
    }

    public void removeThoiMien(Player player) {
        player.effectSkill.isThoiMien = false;
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, SLEEP_EFFECT);
    }

    public void startStun(Player player, long lastTimeStartBlind, int timeBlind) {
        player.effectSkill.lastTimeStartStun = lastTimeStartBlind;
        player.effectSkill.timeStun = timeBlind;
        player.effectSkill.isStun = true;
        sendEffectPlayer(player, player, TURN_ON_EFFECT, BLIND_EFFECT);
    }

    public void removeStun(Player player) {
        player.effectSkill.isStun = false;
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, BLIND_EFFECT);
    }

    public void setSocola(Player player, long lastTimeSocola, int timeSocola) {
        player.effectSkill.lastTimeSocola = lastTimeSocola;
        player.effectSkill.timeSocola = timeSocola;
        player.effectSkill.isSocola = true;
        player.effectSkill.countPem1hp = 0;
    }

    public void removeSocola(Player player) {
        player.effectSkill.isSocola = false;
        Service.getInstance().Send_Caitrang(player);
    }

    public void sendMobToSocola(Player player, Mob mob, int timeSocola) {
        sendMessage(player, -112, msg -> {
            msg.writer().writeByte(1);
            msg.writer().writeByte(mob.id);
            msg.writer().writeShort(4133);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
        });
        mob.effectSkill.setSocola(System.currentTimeMillis(), timeSocola);
    }

    public void setBlindDCTT(Player player, long lastTimeDCTT, int timeBlindDCTT) {
        player.effectSkill.isBlindDCTT = true;
        player.effectSkill.lastTimeBlindDCTT = lastTimeDCTT;
        player.effectSkill.timeBlindDCTT = timeBlindDCTT;
    }

    public void removeBlindDCTT(Player player) {
        player.effectSkill.isBlindDCTT = false;
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, BLIND_EFFECT);
    }

    public void setStartHuytSao(Player player, int tiLeHP) {
        player.effectSkill.tiLeHPHuytSao = tiLeHP;
        player.effectSkill.lastTimeHuytSao = System.currentTimeMillis();
    }

    public void removeHuytSao(Player player) {
        player.effectSkill.tiLeHPHuytSao = 0;
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, HUYT_SAO_EFFECT);
        Service.getInstance().point(player);
        Service.getInstance().Send_Info_NV(player);
    }

    public void setIsMonkey(Player player) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        int timeMonkey = SkillUtil.getTimeMonkey(player.playerSkill.skillSelect.point);
        if (player.setClothes.cadic2 == 5) timeMonkey *= 5;
        if (player.setClothes.cadic1 == 5) timeMonkey *= 2;
        player.effectSkill.isMonkey = true;
        player.effectSkill.timeMonkey = timeMonkey;
        player.effectSkill.lastTimeUpMonkey = System.currentTimeMillis();
        player.effectSkill.levelMonkey = (byte) player.playerSkill.skillSelect.point;
        player.nPoint.setHp(player.nPoint.hp * 2);
    }

    public void monkeyDown(Player player) {
        player.effectSkill.isMonkey = false;
        player.effectSkill.levelMonkey = 0;
        if (player.nPoint.hp > player.nPoint.hpMax) player.nPoint.setHp(player.nPoint.hpMax);
        sendEffectEndCharge(player);
        sendEffectMonkey(player);
        Service.getInstance().setNotMonkey(player);
        Service.getInstance().Send_Caitrang(player);
        Service.getInstance().point(player);
        PlayerService.gI().sendInfoHpMp(player);
        Service.getInstance().Send_Info_NV(player);
        Service.getInstance().sendInfoPlayerEatPea(player);
    }

    public void startCharge(Player player) {
        if (!player.effectSkill.isCharging) {
            player.effectSkill.isCharging = true;
            sendEffectCharge(player);
        }
    }

    public void stopCharge(Player player) {
        player.effectSkill.countCharging = 0;
        player.effectSkill.isCharging = false;
        sendEffectStopCharge(player);
    }

    public void setStartShield(Player player) {
        player.effectSkill.isShielding = true;
        player.effectSkill.lastTimeShieldUp = System.currentTimeMillis();
        player.effectSkill.timeShield = SkillUtil.getTimeShield(player.playerSkill.skillSelect.point);
    }

    public void removeShield(Player player) {
        player.effectSkill.isShielding = false;
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, SHIELD_EFFECT);
    }

    public void breakShield(Player player) {
        removeShield(player);
        Service.getInstance().sendThongBao(player, "Khiên năng lượng đã bị vỡ!");
        ItemTimeService.gI().removeItemTime(player, 3784);
    }

    public void sendEffectBlindThaiDuongHaSan(Player plUseSkill, List<Player> players, List<Mob> mobs, int timeStun) {
        sendMessage(plUseSkill, -45, msg -> {
            msg.writer().writeByte(0);
            msg.writer().writeInt((int) plUseSkill.id);
            msg.writer().writeShort(plUseSkill.playerSkill.skillSelect.skillId);
            msg.writer().writeByte(mobs.size());
            for (Mob mob : mobs) {
                msg.writer().writeByte(mob.id);
                msg.writer().writeByte(timeStun / 1000);
            }
            msg.writer().writeByte(players.size());
            for (Player pl : players) {
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeByte(timeStun / 1000);
            }
            Service.getInstance().sendMessAllPlayerInMap(plUseSkill, msg);
        });
    }

    public void sendEffectStartCharge(Player player) {
        Skill skill = SkillUtil.getSkillbyId(player, Skill.TAI_TAO_NANG_LUONG);
        sendMessage(player, -45, msg -> {
            msg.writer().writeByte(6);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(skill.skillId);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
        });
    }

    public void sendEffectCharge(Player player) {
        Skill skill = SkillUtil.getSkillbyId(player, Skill.TAI_TAO_NANG_LUONG);
        sendMessage(player, -45, msg -> {
            msg.writer().writeByte(1);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(skill.skillId);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
        });
    }

    public void sendEffectStopCharge(Player player) {
        sendMessage(player, -45, msg -> {
            msg.writer().writeByte(3);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(-1);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
        });
    }

    public void sendEffectEndCharge(Player player) {
        sendMessage(player, -45, msg -> {
            msg.writer().writeByte(5);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(player.playerSkill.skillSelect.skillId);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
        });
    }

    public void sendEffectMonkey(Player player) {
        Skill skill = SkillUtil.getSkillbyId(player, Skill.BIEN_KHI);
        if (skill == null) {
            Service.getInstance().sendThongBao(player, "Errorrr");
            return;
        }
        sendMessage(player, -45, msg -> {
            try{
                msg.writer().writeByte(6);
                msg.writer().writeInt((int) player.id);
                msg.writer().writeShort(skill.skillId);
            }
            catch (IOException ex){
                Log.error(ex.getMessage());
            }
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
        });
    }

    private void sendMessage(Player player, int cmd, Consumer<Message> consumer) {
        this.player = player;
        try (Message msg = new Message(cmd)) {
            consumer.accept(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}